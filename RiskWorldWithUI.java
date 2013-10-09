package riskworld;

import java.awt.Color;

import javax.swing.JFrame;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.portrayal.geo.GeomPortrayal;
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
	JFrame globalPriceChartFrame;
	
	public RiskWorldWithUI() {
		super(new RiskWorld(System.currentTimeMillis()));
	}
	
	public void init(Controller controller) {
		super.init(controller);
		RiskWorld world = (RiskWorld)state;
		display = new Display2D(world.worldWidth, world.worldHeight, this, 1);
		
		display.attach(mapPortrayal, "Map");
		display.attach(crisisPortrayal, "Crises");
		display.attach(capitalPortrayal, "Capitals");
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
		display.attach(capitalPortrayal, "Capitals");
		display.attach(netPortrayal, "Trade Network");
		display.attach(adjPortrayal, "Adjacency map");
		
		if (globalPriceChartFrame != null) {
			controller.unregisterFrame(globalPriceChartFrame);
			globalPriceChartFrame.dispose();
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
		
		capitalPortrayal.setField(world.capitals);
		capitalPortrayal.setPortrayalForAll(new OvalPortrayal2D(Color.BLACK, 4.0));
		
		netPortrayal.setField(world.networkMap);
		netPortrayal.setPortrayalForAll(new CustomPortrayals.EdgePortrayal(Color.BLACK, 10));
		
		adjPortrayal.setField(world.adjNetMap);
		adjPortrayal.setPortrayalForAll(new CustomPortrayals.EdgePortrayal(Color.BLACK, 30));
		
		display.setBackdrop(new Color(0,35,100));	
	}
	
	public void setupCharts() {
		
		TimeSeriesChartGenerator chartGen = new TimeSeriesChartGenerator();
		chartGen.setTitle("Global Demand/Supply Ratio");
		chartGen.setXAxisLabel("Steps");
		chartGen.setYAxisLabel("Ratio");
		chartGen.addSeries(((RiskWorld)state).tm.globalRatio, null);
		
		globalPriceChartFrame = chartGen.createFrame();
		globalPriceChartFrame.setTitle("Price proxy");
		globalPriceChartFrame.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		controller.registerFrame(globalPriceChartFrame);
	}
	
	public static void main(String[] args) {
		RiskWorldWithUI sim = new RiskWorldWithUI();
		Console console = new Console(sim);
		console.setVisible(true);
	}

}
