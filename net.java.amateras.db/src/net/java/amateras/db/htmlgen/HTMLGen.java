package net.java.amateras.db.htmlgen;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.java.amateras.db.util.IOUtils;
import net.java.amateras.db.visual.generate.HTMLGenerator;
import net.java.amateras.db.visual.generate.VelocityUtils;
import net.java.amateras.xstream.XStreamSerializer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HTMLGen {

	private static ResourceBundle bundle = ResourceBundle.getBundle(HTMLGenerator.class.getName());
	private static Map<String, String> messages = new HashMap<String, String>();
	static {
		for(Enumeration<String> e = bundle.getKeys();e.hasMoreElements();){
			String key = e.nextElement();
			messages.put(key, bundle.getString(key));
		}
	}

	static {
		// kills Velocity logging
		Velocity.addProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
		        NullLogChute.class.getName());
	}

	private void processTemplate(String templateName, File output, VelocityContext context) throws Exception {
		StringWriter writer = new StringWriter();

		InputStreamReader reader = new InputStreamReader(
				HTMLGenerator.class.getResourceAsStream(templateName), "UTF-8");
		Velocity.evaluate(context, writer, null, reader);

		FileOutputStream out = new FileOutputStream(output);
		out.write(writer.getBuffer().toString().getBytes("UTF-8"));
		IOUtils.close(out);

		IOUtils.close(reader);
		IOUtils.close(writer);
	}

	public void generate(String rootDir, RootModel root) throws Exception {
		IOUtils.copyStream(HTMLGenerator.class.getResourceAsStream("stylesheet.css"),
				new FileOutputStream(new File(rootDir, "stylesheet.css")));

		Velocity.init();
		VelocityContext context = new VelocityContext();
		context.put("model", root);
		context.put("util", new VelocityUtils());
		context.put("msg", messages);

		processTemplate("index.html", new File(rootDir, "index.html"), context);
		processTemplate("list.html", new File(rootDir, "list.html"), context);
		processTemplate("summary.html", new File(rootDir, "summary.html"), context);

		File imageDir = new File(rootDir, "images");
		imageDir.mkdir();

		IOUtils.copyStream(HTMLGenerator.class.getResourceAsStream("primarykey.gif"),
				new FileOutputStream(new File(imageDir, "primarykey.gif")));

		File tableDir = new File(rootDir, "tables");
		tableDir.mkdir();

		for(TableModel table: root.getTables()){
			context.put("table", table);
			processTemplate("table.html", new File(tableDir,
					table.getTableName() + ".html"), context);
		}
	}

	private static List<String> REMOVE_TAGS = new ArrayList<String>();
	static {
		REMOVE_TAGS.add("backgroundColor");
		REMOVE_TAGS.add("listeners");
		REMOVE_TAGS.add("fontData");
		REMOVE_TAGS.add("constraint");
	}

	public static void main(String[] args) throws Exception {
		if(args.length == 0){
			System.out.println("Usage:");
			System.out.println(" java -jar htmlgen.jar erdFile [outputDir]");
			return;
		}

		String erdFile = args[0];
		String outputDir = ".";

		if(args.length >= 2){
			outputDir = args[1];
		}

		String xml = IOUtils.loadStream(new FileInputStream(erdFile), "UTF-8");
		xml = xml.replace("net.java.amateras.db.visual.model.", "net.java.amateras.db.htmlgen.");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
		processElement(doc.getDocumentElement());

		xml = toString(doc);

		RootModel root = (RootModel) XStreamSerializer.deserialize(xml, HTMLGen.class.getClassLoader());

		HTMLGen gen = new HTMLGen();
		gen.generate(outputDir, root);
	}

	private static void processElement(Element e){
		NodeList children = e.getChildNodes();
		for(int i=0; i < children.getLength(); i++){
			Node node = children.item(i);
			if(node instanceof Element){
				if(REMOVE_TAGS.contains(node.getNodeName())){
					e.removeChild(node);
				} else {
					processElement((Element) node);
				}
			}
		}
	}

	private static String toString(Document doc) throws Exception {
		StringWriter sw = new StringWriter();
		TransformerFactory tfactory = TransformerFactory.newInstance();
		Transformer transformer = tfactory.newTransformer();
		transformer.transform(new DOMSource(doc), new StreamResult(sw));
		return sw.toString();
	}

}
