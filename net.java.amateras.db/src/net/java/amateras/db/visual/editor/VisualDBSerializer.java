package net.java.amateras.db.visual.editor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import net.java.amateras.db.util.IOUtils;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.xstream.XStreamSerializer;

public class VisualDBSerializer {

	public static InputStream serialize(RootModel model) throws UnsupportedEncodingException {
		return XStreamSerializer.serializeStream(model, VisualDBSerializer.class.getClassLoader());
	}
	
	public static RootModel deserialize(InputStream in) throws UnsupportedEncodingException {
		String xml = IOUtils.loadStream(in, "UTF-8");
		
		// 1.0.2 -> 1.0.3
		xml = xml.replaceAll(
				"net\\.java\\.amateras\\.db\\.view\\.dialect\\.ColumnType", 
				"net.java.amateras.db.dialect.ColumnType");
		
		return (RootModel)XStreamSerializer.deserialize(
				new ByteArrayInputStream(xml.getBytes("UTF-8")), 
				VisualDBSerializer.class.getClassLoader());
	}
	
}
