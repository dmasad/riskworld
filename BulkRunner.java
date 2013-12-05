package riskworld;

import java.io.FileWriter;
import java.util.HashMap;

import org.jfree.data.xy.XYSeries;

import com.google.gson.Gson;

/**
 * Class for running the main model experiment in bulk mode, saving the outputs.
 * @author dmasad
 *
 */
public class BulkRunner {
	
	/**
	 * Class to store the output of a single model run.
	 *
	 */
	static class ModelOutput {
		boolean contagion;
		boolean assistance;
		double globalSupplyRatio[];
		double globalDemandRatio[];
		double globalOverallRatio[];
		HashMap<String, double[]> supplyRatios;
		HashMap<String, double[]> demandRatios;
		
		ModelOutput(RiskWorld world) {
			contagion = world.contagion;
			assistance = world.assistance;
			TradeMonitor tm = world.tm;
			int ticks = (int)world.schedule.getSteps();
			globalSupplyRatio = convertXYSeries(tm.globalSupplyRatio);
			globalDemandRatio = convertXYSeries(tm.globalDemandRatio);
			globalOverallRatio = convertXYSeries(tm.globalOverallRatio);
			supplyRatios = new HashMap<String, double[]>();
			demandRatios = new HashMap<String, double[]>();
			for (Country c : world.allCountries.values()) {
				supplyRatios.put(c.name, convertXYSeries(c.supplyRatioSeries));
				demandRatios.put(c.name, convertXYSeries(c.demandRatioSeries));
			}
			
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
		int n = 250; // Number of model runs:
		int j = 0; // Current iteration counter
		int numSteps = 60;
		Gson gson = new Gson();
		ModelOutput[] outputs = new ModelOutput[n*4];
		RiskWorld w = new RiskWorld(System.currentTimeMillis());
		boolean[] vals = {true, false};
		for (boolean contagion : vals) {
			for (boolean assistance : vals) {
				for (int i=0; i<n; i++) {
					w.contagion = contagion;
					w.assistance = assistance;
					w.start();
					for (int t=0; t<numSteps; t++)
						w.schedule.step(w);
					
					outputs[j] = new ModelOutput(w);
					j++;
					if (i%20 == 0) System.out.println(i);
				}
			}
		}
		
		System.out.println("Exporting data...");
		String outJson = gson.toJson(outputs);
		exportJSON("RiskOut.json", outJson);
		System.out.println("DONE!");

	}

}
