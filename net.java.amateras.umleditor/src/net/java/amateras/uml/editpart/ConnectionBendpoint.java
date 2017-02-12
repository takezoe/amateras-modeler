package net.java.amateras.uml.editpart;

import java.io.Serializable;

import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

/**
 * 
 * @author Naoki Takezoe
 */
public class ConnectionBendpoint implements Serializable, Bendpoint {
	
	private float weight = 0.5f;

    private Dimension d1 = null;

    private Dimension d2 = null;

    public ConnectionBendpoint() {
    	// ignore
    }

    public ConnectionBendpoint(Dimension dim1, Dimension dim2) {
    	d1 = dim1;
        d2 = dim2;
    }

    public Dimension getFirstRelativeDimension() {
        return d1;
    }

    @Override
    public Point getLocation() {
        return null;
    }

    public Dimension getSecondRelativeDimension() {
        return d2;
    }

    public float getWeight() {
        return weight;
    }

    public void setRelativeDimensions(Dimension dim1, Dimension dim2) {
        d1 = dim1;
        d2 = dim2;
    }

    public void setWeight(float w) {
        weight = w;
    }
}
