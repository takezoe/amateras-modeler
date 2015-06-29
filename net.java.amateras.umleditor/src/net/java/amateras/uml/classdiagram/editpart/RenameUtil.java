package net.java.amateras.uml.classdiagram.editpart;

import java.util.List;

import net.java.amateras.uml.classdiagram.model.Argument;
import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.model.RootModel;

/**
 *
 * @author Naoki Takezoe
 */
public class RenameUtil {

	public static void rename(String from, String to, RootModel root) {
		List<AbstractUMLModel> chidlren = root.getChildren();
		for(int i=0;i<chidlren.size();i++){
			AbstractUMLModel obj = chidlren.get(i);
			if(obj instanceof AbstractUMLEntityModel){
				processEntity(from, to, (AbstractUMLEntityModel)obj);
			}
		}
	}

	private static void processEntity(String from, String to, AbstractUMLEntityModel model){
		List<AbstractUMLModel> children = model.getChildren();
		for(int i=0;i<children.size();i++){
			AbstractUMLModel obj = children.get(i);
			if(obj instanceof AttributeModel){
				AttributeModel attr = (AttributeModel)obj;
				if(attr.getType().equals(from)){
					attr.setType(to);
				}
			} else if(obj instanceof OperationModel){
				OperationModel ope = (OperationModel)obj;
				if(ope.getType().equals(from)){
					ope.setType(to);
				}
				List<Argument> params = ope.getParams();
				for(int j=0;j<params.size();j++){
					Argument arg = (Argument)params.get(j);
					if(arg.getType().equals(from)){
						arg.setType(to);
					}
				}
				ope.setParams(params);
			}
		}
	}

}
