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
		boolean contagion;
		double globalSupplyRatio[];
		double globalDemandRatio[];
		double globalOverallRatio[];
		HashMap<String, double[]> supplyRatios;
		HashMap<String, double[]> demandRatios;
		
		ModelOutput(RiskWorld world) {
			contagion = world.contagion;
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
	 * @param args
	 */
	public static void main(String[] args) {
		int n = 10; // Number of model runs:
		int numSteps = 60;
		Gson gson = new Gson();
		ModelOutput[] outputs = new ModelOutput[n];
		RiskWorld w = new RiskWorld(System.currentTimeMillis());
		for (int i=0; i<n; i++) {
			w.start();
			for (int t=0; t<numSteps; t++)
				w.schedule.step(w);
			outputs[i] = new ModelOutput(w);
		}
		System.out.println("Exporting data...");
		String outJson = gson.toJson(outputs);
		exportJSON("RiskOut.json", outJson);
		System.out.println("DONE!");

	}

}
