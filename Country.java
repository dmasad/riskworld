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
	

}
