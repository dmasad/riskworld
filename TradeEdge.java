package riskworld;
/**
 * Class to represent the export relationship between two countries.
 * @author dmasad
 *
 */
public class TradeEdge {
	// The two countries associated with the edge
	Country exporter;
	Country importer;

	// The amount of oil exported in the baseline, and currently
	double baseSize;
	double currentSize;
	
	// The fraction this edge represents of the exporter's and importer's totals.
	double exportFraction;
	double importFraction;
	
	TradeEdge(Country src, Country dest, double size) {
		exporter = src;
		importer = dest;
		baseSize = size;
		currentSize = baseSize;
	}	

}
