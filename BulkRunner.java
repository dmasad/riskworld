package riskworld;

import java.io.FileWriter;
import java.util.HashMap;

import org.jfree.data.xy.XYSeries;

import com.google.gson.Gson;

/**
 * Class for running the model in bulk mode, saving the outputs.
 * @author dmasad
 *
 */
public class BulkRunner {
	
	/**
	 * Class to store the output of a single model run.
	 *
	 */
	static class ModelOutput {
		double globalRatio[];
		HashMap<String, double[]> localRatios;
		
		ModelOutput(RiskWorld world) {
			TradeMonitor tm = world.tm;
			int ticks = (int)world.schedule.getSteps();
			globalRatio = convertXYSeries(tm.globalRatio);
			localRatios = new HashMap<String, double[]>();
			for (Country c : world.allCountries.values())
				localRatios.put(c.name, convertXYSeries(c.ratioSeries));
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
	 * @param args
	 */
	public static void main(String[] args) {
		int n = 50; // Number of model runs:
		Gson gson = new Gson();
		ModelOutput[] outputs = new ModelOutput[n];
		RiskWorld w = new RiskWorld(System.currentTimeMillis());
		for (int i=0; i<n; i++) {
			w.start();
			for (int t=0; t<60; t++)
				w.schedule.step(w);
			outputs[i] = new ModelOutput(w);
		}
		System.out.println("Exporting data...");
		String outJson = gson.toJson(outputs);
		exportJSON("RiskOut.json", outJson);
		System.out.println("DONE!");

	}

}
