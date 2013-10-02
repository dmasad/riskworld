package riskworld;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.HashMap;

import com.vividsolutions.jts.geom.Envelope;

import sim.engine.SimState;
import sim.field.geo.GeomVectorField;
import sim.field.network.Network;
import sim.io.geo.ShapeFileImporter;
import sim.util.Bag;
import sim.util.geo.MasonGeometry;

public class RiskWorld extends SimState {
	// Model Parameters:
	int worldHeight = 400;
	int worldWidth = 900;
	
	// Storage
	HashMap<String,Country> allCountries;
	Network tradeNetwork;
	
	// Geometries
	GeomVectorField map = new GeomVectorField(worldHeight, worldWidth);
	GeomVectorField networkMap = new GeomVectorField(worldHeight, worldWidth);
	
	// Data files:
	String shapePath = "world_country_boundary_file_with_fips.shp";
	String edgePath = "export_edges.csv";
	
	Utilities util = new Utilities();
	
	public RiskWorld(long seed) {
		super(seed);
		loadMap();
		loadEdges();
		
		Envelope MBR = map.getMBR();
		networkMap.setMBR(MBR);
	}
	
	public void start() {
		super.start();
		schedule.clear();
		for (Country c : allCountries.values()) {
			schedule.scheduleRepeating(c);
			c.initIndustry();
		}
	
	}
	
	/**
	 * Load the world map shapefile and create a Country agent for each geometry.
	 */
	private void loadMap() {
		// Load the map:
		System.out.print("Loading map...");
		URL mapData = RiskWorld.class.getResource("data/" + shapePath);
		try {
			ShapeFileImporter.read(mapData, map);
		} catch (Exception e) {
			System.out.println("Map not found!");
		}
		allCountries = new HashMap<String,Country>();
		Bag states = map.getGeometries();
		for (Object o : states) {
			MasonGeometry state = (MasonGeometry)o;
			Country newCountry = new Country(this, state);
			state.setUserData(newCountry);
			String name = state.getStringAttribute("name sort");
			allCountries.put(name, newCountry);
			schedule.scheduleRepeating(newCountry);
		}
		System.out.print("Done!\n");
	}
	
	/**
	 * Load the export edges and put them in the network.
	 */
	private void loadEdges() {
		System.out.println("Loading trade network...");
		tradeNetwork = new Network();
		String line = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader("/Users/dmasad/Programming/workspace/MasonProjects/src/riskworld/data/" + edgePath));
			while((line = reader.readLine()) != null) {
				String[] row = line.split("\t");
				String src = row[0];
				String trgt = row[1];
				long val = Long.parseLong(row[2]); //Integer.parseInt(row[2]);
				Country source = allCountries.get(src);
				Country target = allCountries.get(trgt);
				if (source != null && target != null) {
					tradeNetwork.addEdge(source, target, val);
					//networkMap.addGeometry(util.makeLine(source.centroid,target.centroid));
					//if (source.name.equals("United States of America"))
						networkMap.addGeometry(util.makeGreatCircleLine(source.centroid, target.centroid));
				}
				else System.out.println("Missing edge: " + line);
			}
			reader.close();
			System.out.println("Network loaded!");
		} catch (Exception e) {
			System.out.println("Error loading network file!");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Main function
	 * @param args
	 */
	public static void main(String[] args) {
		doLoop(RiskWorld.class, args);
		System.exit(0);
	}

}
