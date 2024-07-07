/**
 * 
 */
package net.java.amateras.uml.sequencediagram.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.java.amateras.uml.model.AbstractUMLConnectionModel;

/**
 * @author Takahiro Shida.
 * 
 */
public class MessageOrderUtil {

	public static void computeMessageOrders(InteractionModel model) {
		List<ActivationModel> activations = model.getActivations();
		List<SyncMessageModel> messages = new ArrayList<SyncMessageModel>();
		for (Iterator<ActivationModel> iter = activations.iterator(); iter.hasNext();) {
			ActivationModel element = iter.next();
			messages.addAll(element.getSyncSourceConnection());
		}

		Collections.sort(messages, new MessageOrderComparator());

		int index = 1;
		for (Iterator<SyncMessageModel> iter = messages.iterator(); iter.hasNext();) {
			SyncMessageModel element = iter.next();
			element.setOrder(index);
			index++;
		}
		
		computeMessageDirection(model);
	}

	private static void computeMessageDirection(InteractionModel model) {
		List<ActivationModel> activations = model.getActivations();
		List<AbstractUMLConnectionModel> messages = new ArrayList<AbstractUMLConnectionModel>();
		for (Iterator<ActivationModel> iter = activations.iterator(); iter.hasNext();) {
			ActivationModel element = iter.next();
			messages.addAll(element.getModelSourceConnections());
		}
		for (Iterator<AbstractUMLConnectionModel> iter = messages.iterator(); iter.hasNext();) {
			MessageModel element = (MessageModel) iter.next();
			element.calcDirection();
		}
	}
	
	static class MessageOrderComparator implements Comparator<SyncMessageModel> {

		public int compare(SyncMessageModel arg0, SyncMessageModel arg1) {
			SyncMessageModel msgSource = arg0;
			SyncMessageModel msgTarget = arg1;
			int source = msgSource.getTarget().getConstraint().y;
			int target = msgTarget.getTarget().getConstraint().y;
			if (source == target) {
				return 0;
			} else if (source < target) {
				return -1;
			} else {
				return 1;
			}
		}

	}
}
