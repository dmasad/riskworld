package riskworld;

import java.awt.Color;

import javax.swing.JFrame;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.portrayal.geo.GeomPortrayal;
import sim.portrayal.geo.GeomVectorFieldPortrayal;

public class RiskWorldWithUI extends GUIState {
	Display2D display;
	JFrame displayFrame;
	
	GeomVectorFieldPortrayal mapPortrayal = new GeomVectorFieldPortrayal();
	GeomVectorFieldPortrayal netPortrayal = new GeomVectorFieldPortrayal();
	
	public RiskWorldWithUI() {
		super(new RiskWorld(System.currentTimeMillis()));
	}
	
	public void init(Controller controller) {
		super.init(controller);
		RiskWorld world = (RiskWorld)state;
		display = new Display2D(world.worldWidth, world.worldHeight, this, 1);
		
		display.attach(mapPortrayal, "Map");
		display.attach(netPortrayal, "Trade Network");
		
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
		//mapPortrayal.setPortrayalForAll(new GeomPortrayal(Color.LIGHT_GRAY, true));
		mapPortrayal.setPortrayalForAll(new CustomPortrayals.CountryPortrayal(Color.LIGHT_GRAY, true));
		
		netPortrayal.setField(world.networkMap);
		//netPortrayal.setPortrayalForAll(new GeomPortrayal(Color.BLACK, true));
		netPortrayal.setPortrayalForAll(new CustomPortrayals.EdgePortrayal(Color.BLACK, true));
		
	}
	
	public static void main(String[] args) {
		RiskWorldWithUI sim = new RiskWorldWithUI();
		Console console = new Console(sim);
		console.setVisible(true);
	}

}
