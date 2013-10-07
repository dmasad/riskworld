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

public class RiskWorldWithUI extends GUIState {
	Display2D display;
	JFrame displayFrame;
	
	GeomVectorFieldPortrayal mapPortrayal = new GeomVectorFieldPortrayal();
	GeomVectorFieldPortrayal capitalPortrayal = new GeomVectorFieldPortrayal();
	
	GeomVectorFieldPortrayal netPortrayal = new GeomVectorFieldPortrayal();
	GeomVectorFieldPortrayal adjPortrayal = new GeomVectorFieldPortrayal();
	
	public RiskWorldWithUI() {
		super(new RiskWorld(System.currentTimeMillis()));
	}
	
	public void init(Controller controller) {
		super.init(controller);
		RiskWorld world = (RiskWorld)state;
		display = new Display2D(world.worldWidth, world.worldHeight, this, 1);
		
		display.attach(mapPortrayal, "Map");
		display.attach(capitalPortrayal, "Capitals");
		display.attach(netPortrayal, "Trade Network");
		display.attach(adjPortrayal, "Adjacency map");
		
		displayFrame = display.createFrame();
		controller.registerFrame(displayFrame);
		displayFrame.setVisible(true);
	}
	
	public void start() {
		super.start();
		setupPortrayals();
	}
	
	private void setupPortrayals() {
		RiskWorld world = (RiskWorld)state;
		
		mapPortrayal.setField(world.map);
		Color base = new Color(180, 180, 180, 180); // Gray-ish
		Color crisis = new Color(200, 0, 0, 180);
		mapPortrayal.setPortrayalForAll(new CustomPortrayals.CountryPortrayal(base, crisis));
		
		capitalPortrayal.setField(world.capitals);
		capitalPortrayal.setPortrayalForAll(new OvalPortrayal2D(Color.BLACK, 4.0));
		
		netPortrayal.setField(world.networkMap);
		netPortrayal.setPortrayalForAll(new CustomPortrayals.EdgePortrayal(Color.BLACK, 10));
		
		adjPortrayal.setField(world.adjNetMap);
		adjPortrayal.setPortrayalForAll(new CustomPortrayals.EdgePortrayal(Color.BLACK, 30));
		
		display.setBackdrop(new Color(0,35,100));
		
	}
	
	public static void main(String[] args) {
		RiskWorldWithUI sim = new RiskWorldWithUI();
		Console console = new Console(sim);
		console.setVisible(true);
	}

}
