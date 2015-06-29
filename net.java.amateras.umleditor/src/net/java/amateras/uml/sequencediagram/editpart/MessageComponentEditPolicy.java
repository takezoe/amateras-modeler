/**
 * 
 */
package net.java.amateras.uml.sequencediagram.editpart;

import java.util.Iterator;
import java.util.List;

import net.java.amateras.uml.sequencediagram.model.ActivationModel;
import net.java.amateras.uml.sequencediagram.model.InstanceModel;
import net.java.amateras.uml.sequencediagram.model.LifeLineModel;
import net.java.amateras.uml.sequencediagram.model.MessageAcceptableModel;
import net.java.amateras.uml.sequencediagram.model.MessageModel;
import net.java.amateras.uml.sequencediagram.model.ReturnMessageModel;
import net.java.amateras.uml.sequencediagram.model.SyncMessageModel;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

/**
 * 削除フローが結構複雑なので切り出し.
 * 
 * @author Takahiro Shida.
 * 
 */
public class MessageComponentEditPolicy extends ComponentEditPolicy {

	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		MessageModel model = (MessageModel) getHost().getModel();
		Command command = createDeleteMessageCommand(model);
		return command;
	}

	private Command createDeleteActivationCommand(ActivationModel model) {
		CompoundCommand command = new CompoundCommand();
		List sourceConnections = model.getSyncSourceConnection();
		for (Iterator iter = sourceConnections.iterator(); iter.hasNext();) {
			MessageModel element = (MessageModel) iter.next();
			command.add(createDeleteMessageCommand(element));
		}
		DeleteActivationCommand activationCommand = new DeleteActivationCommand();
		activationCommand.setModel(model);
		command.add(activationCommand);
		List syncTargetConnection = model.getSyncTargetConnection();
		for (Iterator iter = syncTargetConnection.iterator(); iter.hasNext();) {
			SyncMessageModel sync = (SyncMessageModel) iter.next();
			ActivationModel source = (ActivationModel) sync.getSource();
			if (source.getSyncTargetConnection().isEmpty()
					&& source.getSyncSourceConnection().size() == 1) {
				DeleteActivationCommand sourceCommand = new DeleteActivationCommand();
				sourceCommand.setModel(source);
				command.add(sourceCommand);
			}
		}
		return command.unwrap();
	}

	private Command createDeleteMessageCommand(MessageModel model) {
		if (model.getTarget() instanceof ActivationModel) {
			Command command = createDeleteActivationCommand((ActivationModel) model
					.getTarget());
			DeleteMessageCommand messageCommand = new DeleteMessageCommand();
			messageCommand.setModel(model);
			return messageCommand.chain(command);
		} else if (model.getTarget() instanceof InstanceModel) {
			Command command = createDeleteInstaciateCommand(
					(InstanceModel) model.getTarget(), model);
			DeleteMessageCommand messageCommand = new DeleteMessageCommand();
			messageCommand.setModel(model);
			return messageCommand.chain(command);
		} else {
			DeleteMessageCommand messageCommand = new DeleteMessageCommand();
			messageCommand.setModel(model);
			return messageCommand;
		}
	}

	private Command createDeleteInstaciateCommand(InstanceModel model,
			MessageModel msg) {
		ActivationModel active = model.getActive();
		CompoundCommand command = new CompoundCommand();
		List sourceConnections = active.getSyncSourceConnection();
		for (Iterator iter = sourceConnections.iterator(); iter.hasNext();) {
			MessageModel element = (MessageModel) iter.next();
			command.add(createDeleteMessageCommand(element));
		}
		DeleteInstanciationCommnad instanceCommand = new DeleteInstanciationCommnad();
		instanceCommand.setOwner(model);
		instanceCommand.setModel(active);
		command.add(instanceCommand);
		ActivationModel source = (ActivationModel) msg.getSource();
		if (source.getSyncTargetConnection().isEmpty()
				&& source.getSyncSourceConnection().size() == 1) {
			DeleteActivationCommand sourceCommand = new DeleteActivationCommand();
			sourceCommand.setModel(source);
			command.add(sourceCommand);
		}
		return command.unwrap();
	}

	private class DeleteInstanciationCommnad extends Command {
		InstanceModel owner;

		ActivationModel model;

		public void setModel(ActivationModel model) {
			this.model = model;
		}
		
		public void setOwner(InstanceModel owner) {
			this.owner = owner;
		}

		public void execute() {
			owner.setActive(null);
			owner.getModel().removeActivation(model);
		}

		public void undo() {
			owner.setActive(model);
			owner.getModel().addActivation(model);
		}
	}

	private class DeleteActivationCommand extends Command {
		ActivationModel model;

		ActivationModel owner;

		LifeLineModel lifeLine;

		ReturnMessageModel returnMessageModel;

		public void setModel(ActivationModel model) {
			this.model = model;
			List connections = model.getModelSourceConnections();
			for (Iterator iter = connections.iterator(); iter.hasNext();) {
				Object element = (Object) iter.next();
				if (element instanceof ReturnMessageModel) {
					returnMessageModel = (ReturnMessageModel) element;
				}
			}
		}

		public void execute() {
			if (!model.isMovable()) {
				return;
			}
			owner = model.getOwner();
			if (owner != null) {
				owner.removeActivation(model);
			} else {
				lifeLine = model.getOwnerLine();
				lifeLine.removeActivation(model);
			}
			if (returnMessageModel != null) {
				returnMessageModel.detachSource();
				returnMessageModel.detachTarget();
			}
		}

		public void undo() {
			if (!model.isMovable()) {
				return;
			}
			if (owner != null) {
				owner.addActivation(model);
			} else {
				lifeLine.addActivation(model);
			}
			if (returnMessageModel != null) {
				returnMessageModel.attachSource();
				returnMessageModel.attachTarget();
			}
		}
	}

	private class DeleteMessageCommand extends Command {

		private MessageModel model;

		public void setModel(MessageModel model) {
			this.model = model;
		}

		public void execute() {
			MessageAcceptableModel activationModel = (MessageAcceptableModel) model
					.getSource();
			MessageAcceptableModel targetModel = (MessageAcceptableModel) model
					.getTarget();
			model.detachSource();
			model.detachTarget();
			// activationModel.computeChild();
			activationModel.computeCaller();
			targetModel.computeCaller();
		}

		public void undo() {
			model.attachSource();
			model.attachTarget();
			MessageAcceptableModel activationModel = (MessageAcceptableModel) model
					.getTarget();
			MessageAcceptableModel targetModel = (MessageAcceptableModel) model
					.getTarget();
			activationModel.computeCaller();
			targetModel.computeCaller();
		}
	}
}
