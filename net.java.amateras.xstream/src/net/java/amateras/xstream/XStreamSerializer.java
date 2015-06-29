package net.java.amateras.xstream;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import com.thoughtworks.xstream.XStream;

public class XStreamSerializer {
	
	public static String serialize(Object obj, ClassLoader loader){
		XStream xstream = new XStream();
		xstream.setClassLoader(loader);
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xstream.toXML(obj);
	}
	
	public static InputStream serializeStream(Object obj, ClassLoader loader) throws UnsupportedEncodingException {
		String xml = serialize(obj, loader);
		return new ByteArrayInputStream(xml.getBytes("UTF-8"));
	}
	
	public static Object deserialize(String xml, ClassLoader loader){
		XStream xstream = new XStream();
		xstream.setClassLoader(loader);
		return xstream.fromXML(xml);
	}
	
	public static Object deserialize(Reader reader, ClassLoader loader){
		XStream xstream = new XStream();
		xstream.setClassLoader(loader);
		return xstream.fromXML(reader);
	}
	
	public static Object deserialize(InputStream in, ClassLoader loader) throws UnsupportedEncodingException {
		XStream xstream = new XStream();
		xstream.setClassLoader(loader);
		return xstream.fromXML(new InputStreamReader(in, "UTF-8"));
	}
}
