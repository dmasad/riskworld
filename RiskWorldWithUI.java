package riskworld;

import java.awt.Color;

import javax.swing.JFrame;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.portrayal.geo.GeomVectorFieldPortrayal;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.util.media.chart.TimeSeriesChartGenerator;

public class RiskWorldWithUI extends GUIState {
	Display2D display;
	JFrame displayFrame;
	
	// Portrayals
	GeomVectorFieldPortrayal mapPortrayal = new GeomVectorFieldPortrayal();
	GeomVectorFieldPortrayal crisisPortrayal = new GeomVectorFieldPortrayal();
	GeomVectorFieldPortrayal capitalPortrayal = new GeomVectorFieldPortrayal();
	
	GeomVectorFieldPortrayal netPortrayal = new GeomVectorFieldPortrayal();
	GeomVectorFieldPortrayal adjPortrayal = new GeomVectorFieldPortrayal();
	
	// Charts:
	JFrame globalSupplyChartFrame;
	JFrame globalDemandChartFrame;
	JFrame globalRatioChartFrame;
	
	public RiskWorldWithUI() {
		super(new RiskWorld(System.currentTimeMillis()));
	}
	
	@SuppressWarnings("deprecation")
	public void init(Controller controller) {
		super.init(controller);
		RiskWorld world = (RiskWorld)state;
		display = new Display2D(world.worldWidth, world.worldHeight, this, 1);
		
		display.attach(mapPortrayal, "Map");
		display.attach(crisisPortrayal, "Crises");
		//display.attach(capitalPortrayal, "Capitals");
		display.attach(netPortrayal, "Trade Network");
		display.attach(adjPortrayal, "Adjacency map");
		
		displayFrame = display.createFrame();
		controller.registerFrame(displayFrame);
		displayFrame.setVisible(true);
	}
	
	public void start() {
		super.start();
		
		display.reset();
		display.detatchAll();
		
		display.attach(mapPortrayal, "Map");
		display.attach(crisisPortrayal, "Crises");
		//display.attach(capitalPortrayal, "Capitals");
		display.attach(netPortrayal, "Trade Network");
		display.attach(adjPortrayal, "Adjacency map");
		
		if (globalSupplyChartFrame != null) {
			controller.unregisterFrame(globalSupplyChartFrame);
			globalSupplyChartFrame.dispose();
		}
		
		setupPortrayals();
		setupCharts();
	}
	

	
	private void setupPortrayals() {
		RiskWorld world = (RiskWorld)state;
		// Base map:
		mapPortrayal.setField(world.map);
		Color base = new Color(180, 180, 180, 180); // Gray-ish
		Color nullColor = new Color(60, 60, 60, 180);
		mapPortrayal.setPortrayalForAll(new CustomPortrayals.CountryPortrayal(base, nullColor));
		
		crisisPortrayal.setField(world.map);
		Color normal = new Color(255, 255, 255, 0);
		Color crisis = new Color(250, 0, 0, 60);
		crisisPortrayal.setPortrayalForAll(new CustomPortrayals.CrisisPortrayal(normal, crisis));
		
		
		//capitalPortrayal.setField(world.capitals);
		//capitalPortrayal.setPortrayalForAll(new OvalPortrayal2D(Color.BLACK, 4.0));
		
		netPortrayal.setField(world.networkMap);
		netPortrayal.setPortrayalForAll(new CustomPortrayals.EdgePortrayal(Color.BLACK, 20));
		
		adjPortrayal.setField(world.adjNetMap);
		adjPortrayal.setPortrayalForAll(new CustomPortrayals.EdgePortrayal(Color.BLACK, 10));
		
		display.setBackdrop(new Color(0,35,100));	
	}
	
	public void setupCharts() {
		// Supply Ratio Chart
		TimeSeriesChartGenerator chartGen = new TimeSeriesChartGenerator();
		chartGen.setTitle("Total Demand / Current Supply");
		chartGen.setXAxisLabel("Steps");
		chartGen.setYAxisLabel("Ratio");
		chartGen.addSeries(((RiskWorld)state).tm.globalSupplyRatio, null);
		
		globalSupplyChartFrame = chartGen.createFrame();
		globalSupplyChartFrame.setTitle("Total Demand / Current Supply");
		globalSupplyChartFrame.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		controller.registerFrame(globalSupplyChartFrame);
		
		// Demand ratio chart
		TimeSeriesChartGenerator chartGen2 = new TimeSeriesChartGenerator();
		chartGen2.setTitle("Current Demand / Total Supply");
		chartGen2.setXAxisLabel("Steps");
		chartGen2.setYAxisLabel("Ratio");
		chartGen2.addSeries(((RiskWorld)state).tm.globalDemandRatio, null);

		globalDemandChartFrame = chartGen2.createFrame();
		globalDemandChartFrame.setTitle("Current Demand / Total Supply");
		globalDemandChartFrame.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		controller.registerFrame(globalDemandChartFrame);
		
		// Overall ratio chart:
		TimeSeriesChartGenerator chartGen3 = new TimeSeriesChartGenerator();
		chartGen3.setTitle("Current Demand / Current Supply");
		chartGen3.setXAxisLabel("Steps");
		chartGen3.setYAxisLabel("Ratio");
		chartGen3.addSeries(((RiskWorld)state).tm.globalOverallRatio, null);

		globalRatioChartFrame = chartGen3.createFrame();
		globalRatioChartFrame.setTitle("Current Demand / Current Supply");
		globalRatioChartFrame.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		controller.registerFrame(globalRatioChartFrame);
		
	}
	
	public Object getSimulationInspectedObject() { return state; }

	
	public static void main(String[] args) {
		RiskWorldWithUI sim = new RiskWorldWithUI();
		Console console = new Console(sim);
		console.setVisible(true);
	}

}
