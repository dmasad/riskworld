package riskworld;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.network.Edge;
import sim.field.network.Network;
import sim.util.Bag;
import sim.util.geo.MasonGeometry;

public class Country implements Steppable {
	
	RiskWorld world;
	
	// Agent characteristics
	String name;
	MasonGeometry shape;
	MasonGeometry centroid;
	MasonGeometry capital;
	
	double totalImports;
	double totalExports;
	double localRatio;
	
	// Crisis model
	double instability;
	boolean inCrisis = false;
	double crisisLength = -1;
	
	public Country() {;}
	
	public Country(RiskWorld world, String name, double instability) {
		this.world = world;
		this.name = name;
		// Instability=100 <=> Approx. 99% chance of crisis occurring once in 24 periods.
		this.instability = instability/100.0 * 0.18;
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
		
		if (inCrisis) {
			percolate();
			crisisLength--;
			if (crisisLength < 0) inCrisis = false;
			
		}
		else crisisTest();
	}
	
	public void initIndustry() {
		Network network = world.tradeNetwork;
		// Imports:
		totalImports = 0;
		Bag inEdges = network.getEdgesIn(this);
		for (Object o : inEdges) {
			Edge e = (Edge)o;
			totalImports += (Long)e.getInfo();
		}
		
		totalExports = 0;
		Bag outEdges = network.getEdgesOut(this);
		for (Object o : outEdges) {
			Edge e = (Edge)o;
			totalExports += (Long)e.getInfo();
		}
		
	}
	
	public void updateRatio() {
		Network network = world.tradeNetwork;
		Bag inEdges = network.getEdgesIn(this);
		double currentImports = 0;
		for (Object o : inEdges) {
			Edge e = (Edge)o;
			Country neighbor = null;
			if (e.getFrom().equals(this)) neighbor = (Country)e.getTo();
			if (e.getTo().equals(this)) neighbor = (Country)e.getFrom();
			if (!neighbor.inCrisis) currentImports += (Long)e.getInfo();
		}
		localRatio = totalImports/currentImports;
	}
	
	
	/**
	 * Randomly check whether country enters crisis or not.
	 */
	public void crisisTest() {
		if(!inCrisis && world.random.nextBoolean(instability)) {
			inCrisis = true;
			crisisLength = Math.ceil(4 * Math.exp(world.random.nextGaussian()));
		}
	}
	
	public void percolate() {
		Bag neighbors = world.adjNetwork.getEdgesOut(this);
		for (Object o : neighbors) {
			Edge e = (Edge)o;
			Country neighbor = null;
			if (e.getFrom().equals(this)) neighbor = (Country)e.getTo();
			else if (e.getTo().equals(this)) neighbor = (Country)e.getFrom();
			else System.out.println("Danger!!");
			Country c = (Country)e.getTo();
			c.crisisTest();
		}
	}
	
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
	public Double getTotalImports() { return totalImports;}
	public Double getTotalExports() {return totalExports;}
	public boolean getInCrisis() {return inCrisis;}
	public void setInCrisis(boolean crisis) {inCrisis = crisis;}
	public double getCrisisLength() {return crisisLength;}
	public void setCrisisLength(double length) {crisisLength = length;}
	public double getLocalRatio() {return localRatio;}

}
