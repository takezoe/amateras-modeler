package net.java.amateras.uml.classdiagram.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * タイプセーフEnumのプロパティを編集するためのPropertyDescriptor。
 * 
 * @author Naoki Takezoe
 */
public class EnumPropertyDescriptor extends PropertyDescriptor {
	
	private Object[] values;
	
	public EnumPropertyDescriptor(Object id,String displayName,Object[] values){
		super(id,displayName);
		this.values = values;
	}
	
    public CellEditor createPropertyEditor(Composite parent) {
    	String [] values = new String[this.values.length];
    	for(int i=0;i<values.length;i++){
    		values[i] = this.values[i].toString();
    	}
    	
        CellEditor editor = new ComboBoxCellEditor(
        		parent, values, SWT.READ_ONLY){
        	public void doSetValue(Object value){
        		int selection = 0;
        		for(int i=0;i<EnumPropertyDescriptor.this.values.length;i++){
        			if(value == EnumPropertyDescriptor.this.values[i] ||
        			   value.equals(EnumPropertyDescriptor.this.values[i])){
        				selection = i;
        				break;
        			}
        		}
       			super.doSetValue(new Integer(selection));
        	}
        	public Object doGetValue(){
        		int selection = ((Integer)super.doGetValue()).intValue();
        		return EnumPropertyDescriptor.this.values[selection];
        	}
        };
        
        if (getValidator() != null)
            editor.setValidator(getValidator());
        
        return editor;
    }

}
