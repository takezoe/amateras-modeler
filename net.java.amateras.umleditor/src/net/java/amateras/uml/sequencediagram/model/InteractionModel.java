/**
 * 
 */
package net.java.amateras.uml.sequencediagram.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.model.NoteModel;
import net.java.amateras.uml.model.RootModel;

import org.eclipse.draw2d.geometry.Rectangle;

/**
 * ëäå›çÏópê}Çï\Ç∑ÉÇÉfÉã.
 * @author Takahiro Shida.
 *
 */
public class InteractionModel extends RootModel {

	public static final String P_INSTANCE = "_children_instance";
	
	public static final String P_ACTIVATION = "_children_activation";
	
	public static final String P_FRAGMENT = "_children_fragment";
	
	public static final String P_LIFELINE = "_children_lifeline";
	
	private List<InstanceModel> instances = new ArrayList<InstanceModel>();
	
	private List<ActivationModel> activations = new ArrayList<ActivationModel>();
	
	private List<FragmentModel> fragments = new ArrayList<FragmentModel>();
	
	private List<LifeLineModel> lifelines = new ArrayList<LifeLineModel>();
	
	public void addInstance(InstanceModel child){
		child.setRoot(this);
		instances.add(child);
		addChild(child);
		firePropertyChange(P_INSTANCE,null,child);
	}
	
	public List<InstanceModel> getInstances(){
		return instances;
	}
	
	public void removeInstance(InstanceModel obj){
		instances.remove(obj);
		removeChild(obj);
		firePropertyChange(P_INSTANCE, obj, null);
		removeLifeLine(obj.getModel());
	}
	
	public void addActivation(ActivationModel child){
		activations.add(child);
		addChild(child);
		firePropertyChange(P_ACTIVATION,null,child);
	}
	
	public List<ActivationModel> getActivations(){
		return activations;
	}
	
	public void removeActivation(ActivationModel obj){
		activations.remove(obj);
		removeChild(obj);
		firePropertyChange(P_ACTIVATION, obj, null);
		for (Iterator<AbstractUMLModel> iter = obj.getChildren().iterator(); iter.hasNext();) {
			ActivationModel element = (ActivationModel) iter.next();
			removeActivation(element);
		}
		obj.getModelSourceConnections().clear();
		obj.getModelTargetConnections().clear();
	}
	
	public void addFragment(FragmentModel child){
		fragments.add(child);
		addChild(child);
		firePropertyChange(P_FRAGMENT,null,child);
	}
	
	public List<FragmentModel> getFragments(){
		return fragments;
	}
	
	public void removeFragment(FragmentModel obj){
		fragments.remove(obj);
		removeChild(obj);
		firePropertyChange(P_FRAGMENT, obj, null);
	}
		
	public void addLifeLine(LifeLineModel child){
		addChild(child);
		lifelines.add(child);
		firePropertyChange(P_LIFELINE,null,child);
		adjustLifeLine();
	}
	
	public List<LifeLineModel> getLifeLines(){
		return lifelines;
	}
	
	public void removeLifeLine(LifeLineModel obj){
		removeChild(obj);
		lifelines.remove(obj);
		firePropertyChange(P_LIFELINE, obj, null);
		for (Iterator<AbstractUMLModel> iter = obj.getChildren().iterator(); iter.hasNext();) {
			ActivationModel element = (ActivationModel) iter.next();
			removeActivation(element);
		}
	}
		
	public void addUMLModel(AbstractUMLModel model) {
		if (model instanceof InstanceModel) {
			addInstance((InstanceModel) model);
		} else if (model instanceof ActivationModel) {
			addActivation((ActivationModel) model);
		} else if (model instanceof FragmentModel) {
			addFragment((FragmentModel) model);
		} else if (model instanceof LifeLineModel) {
			addLifeLine((LifeLineModel) model);
		} else {
			super.addChild(model);
		}
	}
	
	public void removeUMLModel(AbstractUMLModel model) {
		if (instances.contains(model)) removeInstance((InstanceModel) model);
		if (activations.contains(model)) removeActivation((ActivationModel) model);
		if (fragments.contains(model)) removeFragment((FragmentModel) model);
		if (lifelines.contains(model)) removeLifeLine((LifeLineModel) model);
	}
	
	public void adjustLifeLine() {
		int max =LifeLineModel.DEFAULT_HEIGHT + InstanceModel.DEFAULT_LOCATION;
		for (Iterator<LifeLineModel> iter = lifelines.iterator(); iter.hasNext();) {
			LifeLineModel element = (LifeLineModel) iter.next();
			for (Iterator<AbstractUMLModel> iterator = element.getChildren().iterator(); iterator.hasNext();) {
				ActivationModel a = (ActivationModel) iterator.next();
				int current = a.getConstraint().y + a.getConstraint().height + 30;
				if (current > max) {
					max = current;
				}
				
			}
		}
		for (Iterator<LifeLineModel> iter = lifelines.iterator(); iter.hasNext();) {
			LifeLineModel element = (LifeLineModel) iter.next();
			Rectangle rectangle = element.getConstraint();
			rectangle.height = max - rectangle.y;
			element.setConstraint(rectangle);
		}		
	}

	public List<NoteModel> getNotes() {
		List<NoteModel> rv = new ArrayList<NoteModel>();
		for (Iterator<AbstractUMLModel> iter = getChildren().iterator(); iter.hasNext();) {
			Object element = (Object) iter.next();
			if (element instanceof NoteModel) {
				rv.add((NoteModel) element);
			}
		}
		return rv;
	}

}
