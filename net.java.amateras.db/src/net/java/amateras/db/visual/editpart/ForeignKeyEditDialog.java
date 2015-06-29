package net.java.amateras.db.visual.editpart;

import java.util.ArrayList;
import java.util.List;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.ForeignKeyMapping;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ForeignKeyEditDialog extends Dialog {
	
	private ForeignKeyMapping[] mappings;
	private String foreignKeyName;
	private ColumnModel[] columns;
	private ForeignKeyMapping[] result;
	
	private List<Combo> combos = new ArrayList<Combo>();
	private Text keyName;
	
	private boolean logicalMode;
	
	public ForeignKeyEditDialog(Shell shell,String foreignKeyName, 
			ForeignKeyMapping[] mappings, ColumnModel[] columns, boolean logicalMode){
		super(shell);
		setShellStyle(getShellStyle()|SWT.RESIZE);
		this.foreignKeyName = foreignKeyName;
		this.mappings = mappings;
		this.columns = columns;
		this.logicalMode = logicalMode;
	}

	protected void constrainShellSize() {
		Shell shell = getShell();
		shell.pack();
		shell.setSize(400, shell.getSize().y);
	}
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText(DBPlugin.getResourceString("dialog.mapping.title"));
		
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2,false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label label = new Label(composite, SWT.NULL);
		label.setText(DBPlugin.getResourceString("dialog.mapping.name"));
		keyName = new Text(composite, SWT.BORDER);
		keyName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		keyName.setText(foreignKeyName);
		
		Group group = new Group(composite, SWT.NULL);
		group.setText(DBPlugin.getResourceString("dialog.mapping.mapping"));
		group.setLayout(new GridLayout(3, false));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		group.setLayoutData(gd);
		
		for(int i=0;i<mappings.length;i++){
			label = new Label(group, SWT.BORDER);
			if(logicalMode){
				label.setText(mappings[i].getTarget().getLogicalName());
			} else {
				label.setText(mappings[i].getTarget().getColumnName());
			}
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			label = new Label(group, SWT.NULL);
			label.setText("=");
			
			Combo combo = new Combo(group, SWT.READ_ONLY);
			combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			for(int j=0;j<columns.length;j++){
				if(logicalMode){
					combo.add(columns[j].getLogicalName());
				} else {
					combo.add(columns[j].getColumnName());
				}
			}
			if(mappings[i].getRefer()!=null){
				if(logicalMode){
					combo.setText(mappings[i].getRefer().getLogicalName());
				} else {
					combo.setText(mappings[i].getRefer().getColumnName());
				}
			}
			combos.add(combo);
		}
		
		if(mappings.length == 0){
			label = new Label(group, SWT.NULL);
			label.setText(DBPlugin.getResourceString("dialog.mapping.noColumns"));
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 3;
		}
		
		return composite;
	}
	
	protected void okPressed() {
		result = new ForeignKeyMapping[combos.size()];
		for(int i=0;i<combos.size();i++){
			Combo combo = combos.get(i);
			result[i] = new ForeignKeyMapping();
			result[i].setTarget(mappings[i].getTarget());
			String columnName = combo.getText();
			int index = 0;
			for(int j=0;j<columns.length;j++){
				if(logicalMode){
					if(columns[j].getLogicalName().equals(columnName)){
						index = j;
						break;
					}
				} else {
					if(columns[j].getColumnName().equals(columnName)){
						index = j;
						break;
					}
				}
			}
			result[i].setRefer(columns[index]);
		}
		this.foreignKeyName = keyName.getText();
		
		super.okPressed();
	}
	
	public String getForeignKeyName(){
		return this.foreignKeyName;
	}
	
	public ForeignKeyMapping[] getMapping(){
		return this.result;
	}

}
