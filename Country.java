package riskworld;

import org.jfree.data.xy.XYSeries;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.network.Edge;
import sim.field.network.Network;
import sim.util.Bag;
import sim.util.geo.MasonGeometry;

@SuppressWarnings("serial")
public class Country implements Steppable {
	
	RiskWorld world;
	
	// Agent characteristics
	String name;
	MasonGeometry shape;
	MasonGeometry centroid;
	MasonGeometry capital;
	
	double totalImports;
	double totalExports;
	
	double domesticShare;
	double totalDemand;
	
	double spareCapacity;
	double totalCapacity;
	
	double supplyRatio;
	double demandRatio;
	XYSeries supplyRatioSeries;
	XYSeries demandRatioSeries;
	boolean supplyShock = false;
	
	// Crisis model
	double instability;
	boolean inCrisis = false;
	double crisisLength = -1;
	int crisisCheckCounter; // Check how many times above baseline the country was crisis-checked
	
	// Supply shock response model
	boolean increasingProduction;
	Country increasingProductionFor = null;
	
	
	public Country() {;}
	
	public Country(RiskWorld world, String name, double instability) {
		this.world = world;
		this.name = name;
		// Instability=100 <=> Approx. 80% chance of crisis occurring once in 24 tests.
		this.instability = (instability/10.0) * 0.065; 
		crisisCheckCounter = 0;
	}
	
	
	public Country(RiskWorld world, MasonGeometry shape) {
		this.world = world;
		this.shape = shape;
		name = this.shape.getStringAttribute("name sort");
		centroid = new MasonGeometry(this.shape.geometry.getCentroid());
		
		// Random instability:
		instability = world.random.nextDouble();
	}
	
	public void setShape(MasonGeometry mg) {
		shape = mg;
		centroid = new MasonGeometry(shape.geometry.getCentroid());
	}
	
	public void step(SimState state) {
		world = (RiskWorld)state;
		// Check whether to enter or leave crisis.
		if (inCrisis) {
			//percolate();
			crisisLength--;
			if (crisisLength < 0) inCrisis = false;
		}
		else crisisTest();
		
		if (world.assistance) increaseProductionCheck();
	}
	
	public void initIndustry() {
		inCrisis = false;
		supplyRatioSeries = new XYSeries(name + "Import Supply/Demand Ratio");
		demandRatioSeries = new XYSeries(name + "Export Supply/Demand Ratio");
		
		Network network = world.tradeNetwork;
		// Imports:
		totalImports = 0;
		Bag inEdges = network.getEdgesIn(this);
		for (Object o : inEdges) {
			Edge e = (Edge)o;
			totalImports += ((TradeEdge)e.getInfo()).baseSize;
		}
		totalDemand = totalImports * 1.0/(1 - domesticShare);
		// Exports
		totalExports = 0;
		Bag outEdges = network.getEdgesOut(this);
		for (Object o : outEdges) {
			Edge e = (Edge)o;
			totalExports +=((TradeEdge)e.getInfo()).baseSize;
		}
		
		// Find edge fractions:
		Bag allEdges = network.getEdges(this, null);
		for (Object o : allEdges) {
			Edge e = (Edge)o;
			TradeEdge te = (TradeEdge)e.getInfo();
			if (te.exporter.equals(this)) 
				te.exportFraction = te.baseSize / totalExports;
			if (te.importer.equals(this))
				te.importFraction = te.baseSize / totalImports;
		}
		
		// TODO: Update with real data
		// PLACEHOLDER ASSUMPTIONS:
		// Oil consumption from imports only 
		// Excess capacity 10% of current.
		spareCapacity = world.defaultExcessCapacity;
		totalCapacity = (1 + spareCapacity) * totalExports;
		
	}
	
	public void updateRatios() {
		Network network = world.tradeNetwork;
		// Get import edges and total countries not in crisis.
		Bag inEdges = network.getEdgesIn(this);
		double currentImports = 0;
		for (Object o : inEdges) {
			Edge e = (Edge)o;
			Country neighbor = (Country)e.getFrom();
			if (!neighbor.inCrisis) currentImports += ((TradeEdge)e.getInfo()).currentSize;
		}
		supplyRatio = totalDemand/currentImports;
		if (supplyRatio > world.shockThreshold) {
			supplyShock = true;
			if (world.verbose) System.out.println(name + " experiencing supply shock");
		}
		else 
			supplyShock = false;
		supplyRatioSeries.add(world.schedule.getSteps(), supplyRatio);
		
		// Get export edges and total countries not in crisis
		Bag outEdges = network.getEdgesOut(this);
		double currentExports = 0;
		for (Object o : outEdges) {
			Edge e = (Edge)o;
			Country neighbor = (Country)e.getTo();
			if (!neighbor.inCrisis) currentExports += ((TradeEdge)e.getInfo()).currentSize;
		}
		demandRatio = currentExports/totalExports;
		demandRatioSeries.add(world.schedule.getSteps(), demandRatio);
	}
	
	
	// BEHAVIORS
	// ==============================================================================
	
	/**
	 * Randomly determine whether country enters crisis or not.
	 */
	public void crisisTest() {
		crisisCheckCounter++;
		if(!inCrisis && world.random.nextBoolean(instability)) {
			inCrisis = true;
			if (world.contagion) spreadContagion();
			//crisisLength = Math.ceil(4 * Math.exp(world.random.nextGaussian()));
			crisisLength = Math.ceil(world.util.randomPowerLaw(1, 800, world.conflictExponent));
		}
	}
	
	/**
	 * Propagate a crisis to neighboring countries.
	 */
	public void spreadContagion() {
		Bag neighbors = world.adjNetwork.getEdgesOut(this);
		Bag checked = new Bag();
		for (Object o : neighbors) {
			Edge e = (Edge)o;
			Country neighbor = null;
			if (e.getFrom().equals(this)) neighbor = (Country)e.getTo();
			else if (e.getTo().equals(this)) neighbor = (Country)e.getFrom();
			else System.out.println("Danger!!");
			if (checked.contains(neighbor)) continue;
			neighbor.crisisTest();
			checked.add(neighbor);
			//Country c = (Country)e.getTo();
			//c.crisisTest();
		}
	}
	
	/**
	 * Check to see if any trade partner is experiencing a supply shock;
	 * if so, increase production with probability in proportion to the fraction of
	 * exports going to that partner. 
	 */
	private void increaseProductionCheck() {
		// Check if previous shocks have abated.
	
		if (increasingProductionFor != null && !increasingProductionFor.supplyShock) {
			increasingProduction = false;
			increasingProductionFor = null;
			setProduction(1);
		}
		// Check whether any trade partners have entered supply shock:
		Bag outEdges = world.tradeNetwork.getEdgesOut(this);
		for (Object o : outEdges) {
			Edge e = (Edge)o;
			TradeEdge te = (TradeEdge)e.getInfo();
			Country partner = (Country)e.getTo();
			if (partner.supplyShock && increasingProduction == false &&
					world.random.nextBoolean(te.exportFraction)) {
				// Increase oil production -- and supply to all neighbors.
				setProduction(1.1);
				increasingProduction = true;
				increasingProductionFor = partner;
				if (world.verbose) System.out.println(name + " increasing production to assist " + partner.name);
			}
		}
	}
	
	/**
	 * Set the exports to a fraction of baseline.
	 * @param fraction: The fraction of base size to set the export edge to.
	 */
	private void setProduction(double fraction) {
		Bag outEdges = world.tradeNetwork.getEdgesOut(this);
		for (Object o : outEdges) {
			Edge e = (Edge)o;
			TradeEdge te = (TradeEdge)e.getInfo();
			te.currentSize = fraction * te.baseSize;
		}
	}
	
	// UTILITIES
	// ==================================================
	
	/**
	 * Get the country's representative point; the capital if it has one, otherwise a
	 * centroid.
	 * @return MasonGeometry of the point to use as the node for networks.
	 */
	public MasonGeometry getPoint() {
		if (capital == null) return centroid;
		else return capital;
	}
	
	// Getters and Setters for inspection
	public String getName() {return name;}
	public double getTotalImports() { return totalImports;}
	public double getTotalExports() {return totalExports;}
	public double getDomesticShare() {return domesticShare;}
	public void setDomesticShare(double newShare) {
		domesticShare = newShare;
		totalDemand = totalImports * 1.0/(1 - domesticShare);
	}
	public double getSpareCapacity() {return spareCapacity;}
	public void setSpareCapacity(double newCapacity) {
		spareCapacity = newCapacity;
		totalCapacity = (1 + spareCapacity) * totalExports;
	}
	public double getTotalDemand() {return totalDemand;}
	public boolean getInCrisis() {return inCrisis;}
	public void setInCrisis(boolean crisis) {inCrisis = crisis;}
	public double getCrisisLength() {return crisisLength;}
	public void setCrisisLength(double length) {crisisLength = length;}
	public int getCrisisCheckCounter() {return crisisCheckCounter;}
	public double getLocalRatio() {return supplyRatio;}
	
	public double getInstability() {
		return instability;
		//return instability/0.065 * 10;
	}
	public void setInstability(double instability) {
		this.instability = (instability/10.0) * 0.065;
	}
	
	
	
	public XYSeries getRatioSeries() {return supplyRatioSeries;}

}
