package riskworld;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.HashMap;

import com.vividsolutions.jts.geom.Envelope;

import sim.engine.SimState;
import sim.field.geo.GeomVectorField;
import sim.field.network.Network;
import sim.field.network.stats.NetworkStatistics;
import sim.io.geo.ShapeFileImporter;
import sim.util.Bag;
import sim.util.geo.MasonGeometry;

public class RiskWorld extends SimState {
	// Model Parameters:
	int worldHeight = 400;
	int worldWidth = 900;
	
	long minEdgeToDisplay = 40000000; // Min export volume to show on the map.
									// 40M captures ~90% of all trade.
	
	// Storage
	HashMap<String,Country> allCountries;
	Network tradeNetwork;
	Network adjNetwork;
	
	TradeMonitor tm;
	
	// Geometries
	GeomVectorField map = new GeomVectorField(worldHeight, worldWidth);
	GeomVectorField capitals = new GeomVectorField(worldHeight, worldWidth);
	
	GeomVectorField networkMap = new GeomVectorField(worldHeight, worldWidth);
	GeomVectorField adjNetMap = new GeomVectorField(worldHeight, worldWidth);
	
	// Data files:
	String fullPath = "/Users/dmasad/Programming/workspace/MasonProjects/src/riskworld/data/";
	String countryPath = "CountryData.csv";
	String shapePath = "world_country_boundary_file_with_fips.shp";
	String edgePath = "export_edges.csv";
	String adjPath = "adjList.csv";
	String capitalPath = "world_capitals.shp";
	
	Utilities util = new Utilities();
	
	public RiskWorld(long seed) {
		super(seed);
		loadCountries();
		loadMap();
		loadCapitals();
		loadEdges();
		loadAdjNetwork();
		//findAdjNetwork();
		
		Envelope MBR = map.getMBR();
		capitals.setMBR(MBR);
		networkMap.setMBR(MBR);
		adjNetMap.setMBR(MBR);
	}
	
	public void start() {
		super.start();
		schedule.clear();
		for (Country c : allCountries.values()) {
			schedule.scheduleRepeating(c);
			c.initIndustry();
			c.inCrisis = false;
			c.crisisLength = -1;
		}
		tm = new TradeMonitor(this);
		schedule.scheduleRepeating(tm);
	}
	
	private void loadCountries() {
		System.out.print("Loading countries...");
		allCountries = new HashMap<String,Country>();
		String line = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fullPath + countryPath));
			line = reader.readLine(); // Toss the headers.
			while ((line = reader.readLine()) != null) {
				String[] row = line.split("\t");
				String name = row[0];
				double instability = Double.parseDouble(row[3]);
				Country newCountry = new Country(this, name, instability);
				allCountries.put(name, newCountry);
				schedule.scheduleRepeating(newCountry);
			}
			System.out.print("Done!\n");
			
		} catch (Exception e) {
			System.out.println("\nError loading country file!");
			e.printStackTrace();
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
		//allCountries = new HashMap<String,Country>();
		Bag states = map.getGeometries();
		for (Object o : states) {
			MasonGeometry state = (MasonGeometry)o;
			String name = state.getStringAttribute("NAME_SORT");
			Country newCountry = allCountries.get(name);
			if (newCountry != null) newCountry.setShape(state);
			state.setUserData(newCountry);
		}
		System.out.print("Done!\n");
	}
	
	private void loadCapitals() {
		System.out.print("Loading capitals...");
		URL capitalData = RiskWorld.class.getResource("data/" + capitalPath);
		try {
			ShapeFileImporter.read(capitalData, capitals);
		} catch (Exception e) {
			System.out.println("Map not found!");
		}
		Bag allCapitals = capitals.getGeometries();
		for (Object o : allCapitals) {
			MasonGeometry g = (MasonGeometry)o;
			String countryName = g.getStringAttribute("COUNTRY");
			Country country = allCountries.get(countryName);
			if (country != null) {
				country.capital = g;
			}
		}
	}
	
	/**
	 * Load the export edges and put them in the network.
	 */
	private void loadEdges() {
		System.out.println("Loading trade network...");
		tradeNetwork = new Network();
		String line = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fullPath + edgePath));
			while((line = reader.readLine()) != null) {
				String[] row = line.split("\t");
				String src = row[0];
				String trgt = row[1];
				long val = Long.parseLong(row[2]); //Integer.parseInt(row[2]);
				Country source = allCountries.get(src);
				Country target = allCountries.get(trgt);
				if (source != null && target != null) {
					tradeNetwork.addEdge(source, target, val);
					if (val < minEdgeToDisplay) continue; // Don't display if too small.
					networkMap.addGeometry(util.makeGreatCircleLine(source.getPoint(), target.getPoint()));
				}
				else System.out.println("Missing edge: " + line);
			}
			reader.close();
			System.out.println("Network loaded!");
			util.writeNetwork(tradeNetwork, fullPath + "tradeNetwork.csv");
		} catch (Exception e) {
			System.out.println("Error loading network file!");
			e.printStackTrace();
		}
	}
	
	private void findAdjNetwork() {
		System.out.print("Finding adjacency network");
		adjNetwork = new Network(false);
		for (Country c : allCountries.values()) {
			if (c.shape == null) System.out.println(c.name);
			Bag neighbors = map.getObjectsWithinDistance(c.shape, 0.5);
			for (Object o : neighbors) {
				Country c2 = (Country)((MasonGeometry)o).getUserData();
				if (c2 != null) {
					adjNetwork.addEdge(c, c2, null);
					adjNetMap.addGeometry(util.makeGreatCircleLine(c.getPoint(), c2.getPoint()));
				}
			}
		}
		util.writeNetwork(adjNetwork, fullPath + adjPath);
	}
	
	private void loadAdjNetwork() {
		adjNetwork = new Network(false);
		String line = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fullPath + adjPath));
			line = reader.readLine(); // Toss the header.
			while ((line = reader.readLine()) != null) {
				String[] row = line.split("\t");
				String srcName = row[0];
				String trgtName = row[1];
				Country source = allCountries.get(srcName);
				Country target = allCountries.get(trgtName);
				adjNetwork.addEdge(source, target, null);
				adjNetMap.addGeometry(util.makeGreatCircleLine(source.getPoint(), target.getPoint()));
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			File f = new File(fullPath + adjPath);
			
			if (!f.exists()) {
				System.out.println("Adjacency network not found!");
				findAdjNetwork();
				
			}
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
