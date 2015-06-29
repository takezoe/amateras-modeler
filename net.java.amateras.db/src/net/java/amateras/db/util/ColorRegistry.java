package net.java.amateras.db.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorRegistry {
	
	private Map<RGB, Color> colors = new HashMap<RGB, Color>();
	
	public Color getColor(RGB rgb){
		Color color = colors.get(rgb);
		if(color == null){
			color = new Color(Display.getDefault(), rgb);
			colors.put(rgb, color);
		}
		return color;
	}
	
	public void dispose(){
		for(Color color: colors.values()){
			color.dispose();
		}
		colors.clear();
	}
	
}
