package net.java.amateras.db.visual.generate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.java.amateras.db.util.IOUtils;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;
import org.eclipse.core.resources.IFile;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

public class HTMLGenerator implements IGenerator {

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

	public void execute(IFile erdFile, RootModel root, GraphicalViewer viewer) {
		try {
			DirectoryDialog dialog = new DirectoryDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
			String rootDir = dialog.open();

			if(rootDir != null){
				generate(rootDir, root);
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
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

	public String getGeneratorName() {
		return "HTML";
	}

}
