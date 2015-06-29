package net.java.amateras.uml;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import net.java.amateras.uml.model.RootModel;
import net.java.amateras.xstream.XStreamSerializer;

public class DiagramSerializer {
	
	public static InputStream serialize(RootModel model) throws UnsupportedEncodingException {
		return XStreamSerializer.serializeStream(model, DiagramSerializer.class.getClassLoader());
	}
	
	public static RootModel deserialize(InputStream in) throws UnsupportedEncodingException {
		return (RootModel)XStreamSerializer.deserialize(in, DiagramSerializer.class.getClassLoader());
	}
}
