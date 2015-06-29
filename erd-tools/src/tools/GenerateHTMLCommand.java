package tools;

import java.io.File;
import java.io.FileInputStream;

import net.java.amateras.db.util.IOUtils;
import net.java.amateras.db.visual.editor.VisualDBSerializer;
import net.java.amateras.db.visual.generate.HTMLGenerator;
import net.java.amateras.db.visual.model.RootModel;

/**
 * A command line tool to generate a HTML report from a diagram file.
 * <p>
 * <strong>Usage:</strong>
 * <pre>tools.GenerateHTMLCommand erd-file export-dir</pre>
 * 
 * @author Naoki Takezoe
 */
public class GenerateHTMLCommand {
	
	public static void main(String[] args) throws Exception {
		if(args.length != 2){
			System.err.println("Please specify the erd file and the export directory!");
			System.exit(1);
		}
		
		File erdFile = new File(args[0]);
		File exportDir = new File(args[1]);
		
		if(!erdFile.exists() || !erdFile.isFile()){
			System.err.println("The specified erd file does not exist!");
			System.exit(1);
		}
		
		if(!exportDir.exists()){
			if(!exportDir.mkdir()){
				System.err.println("Failed to create the export directory!");
				System.exit(1);
			}
		}
		
		System.out.println("** Start to generate HTML report **");
		
		FileInputStream in = null;
		try {
			in = new FileInputStream(erdFile);
			RootModel rootModel = VisualDBSerializer.deserialize(in);
			
			HTMLGenerator generator = new HTMLGenerator();
			generator.generate(exportDir.getAbsolutePath(), rootModel);
			
		} finally {
			IOUtils.close(in);
		}
		
		System.out.println("** Completed! **");
	}
	
}
