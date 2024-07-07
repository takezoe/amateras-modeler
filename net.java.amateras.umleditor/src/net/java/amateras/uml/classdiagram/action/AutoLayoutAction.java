package net.java.amateras.uml.classdiagram.action;

import java.util.List;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.action.AbstractUMLEditorAction;
import net.java.amateras.uml.editpart.AbstractUMLEntityEditPart;
import net.java.amateras.uml.model.AbstractUMLConnectionModel;
import net.java.amateras.uml.model.AbstractUMLEntityModel;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.EdgeList;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.NodeList;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * The action to layout all entities automatically.
 *
 * @author Naoki Takezoe
 * @see DirectedGraph
 * @see DirectedGraphLayout
 */
public class AutoLayoutAction extends AbstractUMLEditorAction {

	/**
	 * Constructor.
	 *
	 * @param viewer the graphical viewer
	 */
	public AutoLayoutAction(GraphicalViewer viewer) {
		super(UMLPlugin.getDefault().getResourceString("menu.autoLayout"), viewer);
	}

	public void update(IStructuredSelection sel) {
		// Do nothing
	}

	@SuppressWarnings("rawtypes")
	public void run() {
		CompoundCommand commands = new CompoundCommand();
		List models = getViewer().getContents().getChildren();
		NodeList graphNodes = new NodeList();
		EdgeList graphEdges = new EdgeList();
		// assemble nodes
		for(int i=0;i<models.size();i++){
			Object obj = models.get(i);
			if(obj instanceof AbstractUMLEntityEditPart){
				AbstractUMLEntityEditPart editPart = (AbstractUMLEntityEditPart) obj;
				AbstractUMLEntityModel model = (AbstractUMLEntityModel) editPart.getModel();
				EntityNode node = new EntityNode();
				node.model = model;
				node.width = editPart.getFigure().getSize().width;
				node.height = editPart.getFigure().getSize().height;
				graphNodes.add(node);
			}
		}
		// assemble edges
		for(int i=0;i<graphNodes.size();i++){
			EntityNode node = (EntityNode) graphNodes.get(i);
			List<AbstractUMLConnectionModel> conns = node.model.getModelSourceConnections();
			CONN_LOOP: for(int j=0;j<conns.size();j++){
				AbstractUMLConnectionModel conn = conns.get(j);
				// skip if the connection already added
				for(int k=0;k<graphEdges.size();k++){
					ConnectionEdge edge = (ConnectionEdge) graphEdges.get(k);
					if(edge.model == conn){
						continue CONN_LOOP;
					}
				}
				EntityNode source = (EntityNode) getNode(graphNodes, conn.getSource());
				EntityNode target = (EntityNode) getNode(graphNodes, conn.getTarget());
				if(source != null && target != null){
					ConnectionEdge edge = new ConnectionEdge(source, target);
					edge.model = conn;
					graphEdges.add(edge);
				}
			}
		}

		DirectedGraph graph = new DirectedGraph();
		graph.nodes = graphNodes;
		graph.edges = graphEdges;
		new DirectedGraphLayout().visit(graph);
		for (int i = 0; i < graph.nodes.size(); i++) {
			EntityNode node = (EntityNode) graph.nodes.get(i);
			commands.add(new LayoutCommand(node.model, node.x, node.y));
		}

		getViewer().getEditDomain().getCommandStack().execute(commands);
	}

	private static EntityNode getNode(NodeList list, AbstractUMLEntityModel model){
		for(int i=0;i<list.size();i++){
			EntityNode node = (EntityNode) list.get(i);
			if(node.model == model){
				return node;
			}
		}
		return null;
	}

	private class EntityNode extends Node {
		private AbstractUMLEntityModel model;
	}

	private class ConnectionEdge extends Edge {
		private AbstractUMLConnectionModel model;
		public ConnectionEdge(EntityNode source, EntityNode target){
			super(source, target);
		}
	}

	/**
	 * Command to relocate the entity model.
	 * This command is executed as a part of CompoundCommand.
	 */
	private class LayoutCommand extends Command {

		private AbstractUMLEntityModel target;
		private int x;
		private int y;
		private int oldX;
		private int oldY;

		public LayoutCommand(AbstractUMLEntityModel target, int x, int y){
			this.target = target;
			this.x = x;
			this.y = y;
			this.oldX = target.getConstraint().x;
			this.oldY = target.getConstraint().y;
		}

		public void execute() {
			this.target.setConstraint(new Rectangle(this.x, this.y, -1, -1));
		}

		public void undo() {
			this.target.setConstraint(new Rectangle(this.oldX, this.oldY, -1, -1));
		}
	}

}
