package riskworld;

import java.io.FileWriter;
import java.util.HashMap;

import org.jfree.data.xy.XYSeries;

import com.google.gson.Gson;

/**
 * Class for running the sensitivity analysis, saving global output and parameters.
 *
 */
public class SensitivityAnalysis {
	
	/**
	 * Class to store the output of a single model run, for global variables only.
	 * NOTE: Different from the Model Output defined in BulkRunner.
	 * 
	 */
	static class ModelOutput {
		boolean contagion;
		boolean assistance;
		double conflictExponent;
		double defaultSpareCapacity;
		double shockThreshold;
		
		double globalSupplyRatio[];
		double globalDemandRatio[];
		double globalOverallRatio[];
		
		ModelOutput(RiskWorld world) {
			contagion = world.contagion;
			assistance = world.assistance;
			conflictExponent = world.conflictExponent;
			defaultSpareCapacity = world.defaultSpareCapacity;
			shockThreshold = world.shockThreshold;
			TradeMonitor tm = world.tm;
			int ticks = (int)world.schedule.getSteps();
			globalSupplyRatio = convertXYSeries(tm.globalSupplyRatio);
			globalDemandRatio = convertXYSeries(tm.globalDemandRatio);
			globalOverallRatio = convertXYSeries(tm.globalOverallRatio);
			
		}
		
		/**
		 * Helper class to convert an XYSeries to a double array.
		 * @param xySeries: XYSeries
		 */
		private double[] convertXYSeries(XYSeries xySeries) {
			int count = xySeries.getItemCount();
			double[] series = new double[count];
			for (int i=0; i<count; i++) {
				double v = xySeries.getY(i).doubleValue();
				if (!Double.isInfinite(v) && !Double.isNaN(v)) series[i] = v; 
			}
				
			return series;
		}
	}
	
	public static void exportJSON(String fileName, String json) {
		try {
			FileWriter writer = new FileWriter(fileName);
			// Write header:
			writer.append(json);
			// Close out:
			writer.flush();
			writer.close();
		} catch(Exception e) {e.printStackTrace();}
		
	}

	/**
	 * MAIN EXPERIMENT
	 * ===============
	 * Run the model for 60 steps each iteration, for n iterations per parameter combination
	 * Turn the Contagion and Assistance booleans on and off.
	 * 
	 */
	public static void main(String[] args) {
		int n = 10; // Number of model runs per permutation
		int j = 0; // Current iteration counter
		int numSteps = 60;
		Gson gson = new Gson();
		ModelOutput[] outputs = new ModelOutput[2200];
		RiskWorld w = new RiskWorld(System.currentTimeMillis());
		for (double conflictExponent=-2; conflictExponent < -1; conflictExponent+=0.05)
			for (double defaultSpareCapacity=0; defaultSpareCapacity<0.5; defaultSpareCapacity+=0.05)
				for (int i=0; i<n; i++) {
					w.conflictExponent = conflictExponent;
					w.defaultSpareCapacity = defaultSpareCapacity;
					w.contagion = true;
					w.assistance = true;
					w.start();
					for (int t=0; t<numSteps; t++)
						w.schedule.step(w);
					
					outputs[j] = new ModelOutput(w);
					j++;
					if (j%100 == 0) System.out.println(j);

				}
		System.out.println(j);
		System.out.println("Exporting data...");
		String outJson = gson.toJson(outputs);
		exportJSON("Risk_SensAnalysis.json", outJson);
		System.out.println("DONE!");

	}

}
