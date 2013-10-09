package riskworld;

import org.jfree.data.xy.XYSeries;

import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Class to monitor the overall state of the network.
 * @author dmasad
 *
 */
public class TradeMonitor implements Steppable {
	RiskWorld world;
	
	// Worldwide supply-demand
	double totalSupply = 0;
	double totalDemand = 0;
	XYSeries globalRatio = new XYSeries("Demand/Supply Ratio");
	
	/**
	 * Constructor; assumes that the rest of the model has already been set up.
	 * @param world
	 */
	public TradeMonitor(RiskWorld world) {
		this.world = world;
		for (Country c : world.allCountries.values()) {
			totalSupply += c.totalExports;
			totalDemand += c.totalImports;
		}
	}
	
	public void step(SimState state) {
		double currentSupply = 0;
		for (Country c : world.allCountries.values()) {
			if (!c.inCrisis) currentSupply += c.totalExports;
			c.updateRatio();
		}
		double ratio = totalDemand/currentSupply;
		globalRatio.add(world.schedule.getSteps(), ratio);
	}
}
