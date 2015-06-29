package net.java.amateras.db.visual.model;

import net.java.amateras.db.DBPlugin;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * This class has been ported from AmaterasUML.
 * 
 * @author Naoki Takezoe
 * @author Takahiro Shida
 * @since 1.0.6
 */
public class NoteModel extends AbstractDBEntityModel {
	
	public static final String P_CONTENT = "p_content";
	
	private String content = "";
	private static final int DEFAULT_WIDTH = 100;
	private static final int DEFAULT_HEIGHT = 30;
	
	@Override
	public boolean canSource(AbstractDBConnectionModel conn) {
		if(conn instanceof ForeignKeyModel){
			return false;
		}
		return true;
	}

	@Override
	public boolean canTarget(AbstractDBConnectionModel conn) {
		if(conn instanceof ForeignKeyModel){
			return false;
		}
		return true;
	}

	public void setConstraint(Rectangle constraint) {
		if (constraint.width < DEFAULT_WIDTH) {
			constraint.width = DEFAULT_WIDTH;
		}
		if (constraint.height < DEFAULT_HEIGHT) {
			constraint.height = DEFAULT_HEIGHT;
		}
		super.setConstraint(constraint);
	}
	
	public void setContent(String content) {
		this.content = content;
		firePropertyChange(P_CONTENT, null, content);
	}
	
	public String getContent() {
		return content;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[]{
				new TextPropertyDescriptor(P_CONTENT, DBPlugin.getResourceString("property.text")),
		};
	}

	public Object getPropertyValue(Object id) {
		if(id.equals(P_CONTENT)){
			return getContent();
		}
		return null;
	}

	public boolean isPropertySet(Object id) {
		if(id.equals(P_CONTENT)){
			return true;
		}
		return false;
	}

	public void setPropertyValue(Object id, Object value) {
		if(id.equals(P_CONTENT)){
			setContent((String)value);
		}
	}
}
