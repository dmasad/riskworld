package riskworld;

import org.jfree.data.xy.XYSeries;

import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Class to monitor the overall state of the network.
 * @author dmasad
 *
 */
@SuppressWarnings("serial")
public class TradeMonitor implements Steppable {
	RiskWorld world;
	
	// Worldwide supply-demand
	double totalSupply = 0;
	double totalDemand = 0;
	XYSeries globalSupplyRatio = new XYSeries("Total Demand / Current Supply");
	XYSeries globalDemandRatio = new XYSeries("Current Demand / Total Supply");
	XYSeries globalOverallRatio = new XYSeries("Current Demand / Current Supply");
	
	double[] allSupplyRatios;
	
	/**
	 * Constructor; assumes that the rest of the model has already been set up.
	 * @param world
	 */
	public TradeMonitor(RiskWorld world) {
		this.world = world;
		for (Country c : world.allCountries.values()) {
			totalSupply += (c.totalExports + c.domesticShare*c.totalDemand);
			totalDemand += c.totalDemand;
		}
	}
	
	public void step(SimState state) {
		double currentSupply = 0;
		double currentDemand = 0;
		allSupplyRatios = new double[world.allCountries.size()];
		int i = 0;
		for (Country c : world.allCountries.values()) {
			currentDemand += (c.domesticShare*c.totalDemand);
			currentSupply += (c.domesticShare * c.totalDemand);
			if (!c.inCrisis) {
				currentSupply += c.totalExports;
				currentDemand += c.totalImports;
			}
			c.updateRatios();
			allSupplyRatios[i] = c.supplyRatio;
			i++;
		}
		world.allSupplyRatios = allSupplyRatios;
		
		long step = world.schedule.getSteps();
		double ratio = totalDemand/currentSupply;
		globalSupplyRatio.add(step, ratio);
		
		ratio = currentDemand/totalSupply;
		globalDemandRatio.add(step, ratio);
		
		ratio = currentDemand/currentSupply;
		globalOverallRatio.add(step, ratio);
	}
}
