package riskworld;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;

import sim.portrayal.DrawInfo2D;
import sim.portrayal.geo.GeomPortrayal;
import sim.util.geo.MasonGeometry;

public class CustomPortrayals {
	
	public static class CountryPortrayal extends GeomPortrayal {
		
		public CountryPortrayal(Paint paint, boolean filled) {
			super(paint, 1.0, filled);
		}
		
		public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
			MasonGeometry geom = (MasonGeometry)object;
			Country country = (Country)geom.getUserData();
			if (country.inCrisis) paint = Color.RED;
			else paint = Color.LIGHT_GRAY; //Color.DARK_GRAY;
			
			super.draw(object, graphics, info);
		}
	}
	
	
	public static class EdgePortrayal extends GeomPortrayal {
		public EdgePortrayal(Paint paint, boolean filled) {
			super(paint, 1.0, filled);
		}
		
		public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
			int alpha = 10;
			//Color c = new Color(255, 255, 255, alpha);
			Color c = new Color(0,0,0,alpha);
			paint = c;
			super.draw(object, graphics, info);
		}
	}

}
