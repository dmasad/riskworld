package riskworld;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.WKTReader;

import sim.util.geo.MasonGeometry;

/**
 * Class to hold helpful utilities apart from the main model class.
 * @author dmasad
 *
 */
public class Utilities {

	WKTReader rdr = new WKTReader();
	
	public MasonGeometry makeLine(MasonGeometry start, MasonGeometry end) {
		Coordinate startCoord = start.geometry.getCoordinate();
		Coordinate endCoord = end.geometry.getCoordinate();
		String startX = Double.toString(startCoord.x);
		String startY = Double.toString(startCoord.y);
		String startStr = startX + " " + startY;
		
		String endX = Double.toString(endCoord.x);
		String endY = Double.toString(endCoord.y);
		String endStr = endX + " " + endY;
		
		String lineStr = "LINESTRING (" + startStr + ", " + endStr + ")";
		LineString line = null;
		try {
			line = (LineString)rdr.read(lineStr);
		} catch (Exception e) {
			System.out.println("Line parse error!");
			line = null;
		}
		return new MasonGeometry(line);
	}
	
	
	// GREAT CIRCLE DRAWING
	// ====================
	
	public MasonGeometry makeGreatCircleLine(MasonGeometry start, MasonGeometry end) {
		Coordinate startCoord = start.geometry.getCoordinate();
		Coordinate endCoord = end.geometry.getCoordinate();
		ArrayList<Coordinate> allCoords = subdivide(startCoord, endCoord, 0, 4);
		String fullStr = "";
		for (Coordinate c : allCoords) {
			String strX = Double.toString(c.x);
			String strY = Double.toString(c.y);
			fullStr += strX + " " + strY + ", ";
		}
		fullStr = fullStr.substring(0, fullStr.length() - 2);

		String lineStr = "LINESTRING (" + fullStr + ")";
		LineString line = null;
		try {
			line = (LineString)rdr.read(lineStr);
		} catch (Exception e) {
			System.out.println("Line parse error!");
			e.printStackTrace();
			line = null;
		}
		return new MasonGeometry(line);
	}
	
	private ArrayList<Coordinate> subdivide(Coordinate start, Coordinate end, int currentDepth, int maxDepth) {
		ArrayList<Coordinate> results = new ArrayList<Coordinate>();
		if (currentDepth == maxDepth) {
			results.add(start);
			results.add(end);
		}
		else {
			Coordinate midpoint = getMidpoint(start, end);
			
			ArrayList<Coordinate> firstHalf = subdivide(start, midpoint, currentDepth+1, maxDepth);
			ArrayList<Coordinate> secondHalf = subdivide(midpoint, end, currentDepth+1, maxDepth);
			
			results.addAll(firstHalf);
			results.addAll(secondHalf);
		}
		return results;
	}
	
	/**
	 * Get the great circle midpoint between two coordinates
	 * @param start
	 * @param end
	 * @return a Coordinate that is halfway on the great circle path
	 */
	private Coordinate getMidpoint(Coordinate start, Coordinate end) {

		double lat1 = start.y;
		double lon1 = start.x;
		double lat2 = end.y;
		double lon2 = end.x;
		double dLon = lon2 - lon1;
		double dLat = lat2 - lat1;
		
		// Get midpoints:
		double Bx = cos(lat2) * cos(dLon);
		double By = cos(lat2) * sin(dLon);
		double midLat = atan2(sin(lat1)+sin(lat2),
                Math.sqrt( (cos(lat1)+Bx)*(cos(lat1)+Bx) + By*By ) );
		double midLon = lon1 + atan2(By, cos(lat1) + Bx);
		return new Coordinate(midLon, midLat);
	}
	
	// Trig functions in degrees.
	private double cos(double theta) { return Math.cos(Math.toRadians(theta)); }
	private double sin(double theta) { return Math.sin(Math.toRadians(theta)); }
	private double atan2(double y, double x) {return Math.toDegrees(Math.atan2(y, x));}
	
	
	
}
