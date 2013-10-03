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
	
	// Crisis model
	double instability;
	boolean inCrisis = false;
	double crisisLength = -1;
	
	public Country(RiskWorld world, MasonGeometry shape) {
		this.world = world;
		this.shape = shape;
		name = this.shape.getStringAttribute("name sort");
		centroid = new MasonGeometry(this.shape.geometry.getCentroid());
		
		// Random instability:
		instability = world.random.nextDouble();
	}
	
	public void step(SimState state) {
		world = (RiskWorld)state;
		
		// Instability model:
		if(!inCrisis && world.random.nextBoolean(instability)) {
			inCrisis = true;
			crisisLength = Math.ceil(4 * Math.exp(world.random.nextGaussian()));
		}
		else if (inCrisis) {
			crisisLength--;
			if (crisisLength==0) inCrisis=false;
		}
		
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
	
	public MasonGeometry getPoint() {
		if (capital == null) return centroid;
		else return capital;
	}
	
	public String getName() {return name;}
	public Double getTotalImports() { return totalImports;}
	public Double getTotalExports() {return totalExports;}
	
	
	/**
	 * Randomly check whether 
	 */
	public void crisisTest() {
		
	}

}
