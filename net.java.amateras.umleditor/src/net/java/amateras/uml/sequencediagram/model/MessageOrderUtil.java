/**
 * 
 */
package net.java.amateras.uml.sequencediagram.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @author Takahiro Shida.
 * 
 */
public class MessageOrderUtil {

	public static void computeMessageOrders(InteractionModel model) {
		List activations = model.getActivations();
		List messages = new ArrayList();
		for (Iterator iter = activations.iterator(); iter.hasNext();) {
			ActivationModel element = (ActivationModel) iter.next();
			messages.addAll(element.getSyncSourceConnection());
		}

		Collections.sort(messages, new MessageOrderComparator());

		int index = 1;
		for (Iterator iter = messages.iterator(); iter.hasNext();) {
			SyncMessageModel element = (SyncMessageModel) iter.next();
			element.setOrder(index);
			index++;
		}
		
		computeMessageDirection(model);
	}

	private static void computeMessageDirection(InteractionModel model) {
		List activations = model.getActivations();
		List messages = new ArrayList();
		for (Iterator iter = activations.iterator(); iter.hasNext();) {
			ActivationModel element = (ActivationModel) iter.next();
			messages.addAll(element.getModelSourceConnections());
		}
		for (Iterator iter = messages.iterator(); iter.hasNext();) {
			MessageModel element = (MessageModel) iter.next();
			element.calcDirection();
		}
	}
	
	static class MessageOrderComparator implements Comparator {

		public int compare(Object arg0, Object arg1) {
			SyncMessageModel msgSource = (SyncMessageModel) arg0;
			SyncMessageModel msgTarget = (SyncMessageModel) arg1;
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
