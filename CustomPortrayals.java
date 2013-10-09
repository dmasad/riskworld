package riskworld;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;

import sim.portrayal.DrawInfo2D;
import sim.portrayal.geo.GeomPortrayal;
import sim.util.geo.MasonGeometry;

public class CustomPortrayals {
	
	
	/**
	 * Differentiate between countries for which we have data and those we don't.
	 * @author dmasad
	 *
	 */
	public static class CountryPortrayal extends GeomPortrayal {
		Color existsPaint;
		Color nullPaint;
		
		public CountryPortrayal(Color existsPaint, Color nullPaint) {
			super(nullPaint, 1.0, true);
			this.existsPaint = existsPaint;
			this.nullPaint = nullPaint;
		}
		
		public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
			MasonGeometry geom = (MasonGeometry)object;
			Country country = (Country)geom.getUserData();
			if (country == null) paint = nullPaint;
			else paint = existsPaint;
			super.draw(object, graphics, info);
		}
	}
	
	public static class CrisisPortrayal extends GeomPortrayal {
		Color basePaint;
		Color crisisPaint;
		public CrisisPortrayal(Color basePaint, Color crisisPaint) {
			super(basePaint, 1.0, true);
			this.basePaint = basePaint;
			this.crisisPaint = crisisPaint;
		}
		
		public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
			MasonGeometry geom = (MasonGeometry)object;
			Country country = (Country)geom.getUserData();
			if (country == null) paint = basePaint;
			else if (country.inCrisis) paint = crisisPaint;
			else paint = basePaint;
			super.draw(object, graphics, info);
		}
	}
	
	
	public static class EdgePortrayal extends GeomPortrayal {
		public EdgePortrayal(Paint paint, int alpha) {
			super(paint, 1.0, true);
			Color p = (Color)paint;
			Color c = new Color(p.getRed(), p.getGreen(), p.getBlue(), alpha);
			this.paint = c;
		}
		
	}

}
