package net.java.amateras.db.visual.generate;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.util.IOUtils;
import net.java.amateras.db.visual.model.AbstractDBConnectionModel;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.ForeignKeyMapping;
import net.java.amateras.db.visual.model.ForeignKeyModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.core.resources.IFile;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.seasar.fisshplate.template.FPTemplate;

public class ExcelGenerator implements IGenerator {

	public static class TableData {
		private String logicalTableName = "";
		private String physicalTableName = "";
		private String description;
		private List<ColumnData> columns = new ArrayList<ColumnData>();
		public String getLogicalTableName() {
			return logicalTableName;
		}
		public void setLogicalTableName(String logicalTableName) {
			this.logicalTableName = logicalTableName;
		}
		public String getPhysicalTableName() {
			return physicalTableName;
		}
		public void setPhysicalTableName(String physicalTableName) {
			this.physicalTableName = physicalTableName;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public List<ColumnData> getColumns() {
			return columns;
		}
		public void setColumns(List<ColumnData> columns) {
			this.columns = columns;
		}
	}

	public static class ColumnData {
		private int index;
		private String logicalColumnName = "";
		private String physicalColumnName = "";
		private String primaryKey = "";
		private String foreignKey = "";
		private String type = "";
		private String size = "";
		private String reference = "";
		private String description = "";
		private String nullable = "";
		private String defaultValue = "";

		public String getNullable() {
			return nullable;
		}
		public void setNullable(String nullable) {
			this.nullable = nullable;
		}
		public String getLogicalColumnName() {
			return logicalColumnName;
		}
		public void setLogicalColumnName(String logicalColumnName) {
			this.logicalColumnName = logicalColumnName;
		}
		public String getPhysicalColumnName() {
			return physicalColumnName;
		}
		public void setPhysicalColumnName(String physicalColumnName) {
			this.physicalColumnName = physicalColumnName;
		}
		public String getPrimaryKey() {
			return primaryKey;
		}
		public void setPrimaryKey(String primaryKey) {
			this.primaryKey = primaryKey;
		}
		public String getForeignKey() {
			return foreignKey;
		}
		public void setForeignKey(String foreignKey) {
			this.foreignKey = foreignKey;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getSize() {
			return size;
		}
		public void setSize(String size) {
			this.size = size;
		}
		public String getReference() {
			return reference;
		}
		public void setReference(String reference) {
			this.reference = reference;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public String getDefaultValue() {
			return defaultValue;
		}
		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}
	}

	public void execute(IFile erdFile, RootModel root, GraphicalViewer viewer) {
		FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
		dialog.setFilterExtensions(new String[]{"*.xls"});
		String path = dialog.open();
		if(path == null){
			return;
		}

		List<TableData> tables = new ArrayList<TableData>();

		for(TableModel table: root.getTables()){
			TableData tableData = new TableData();
			tableData.setLogicalTableName(table.getLogicalName());
			tableData.setPhysicalTableName(table.getTableName());
			tableData.setDescription(table.getDescription());

			List<ColumnData> columns = new ArrayList<ColumnData>();
			for(ColumnModel column: table.getColumns()){
				ColumnData columnData = new ColumnData();
				columnData.setLogicalColumnName(column.getLogicalName());
				columnData.setPhysicalColumnName(column.getColumnName());
				columnData.setDescription(column.getDescription());
				columnData.setType(column.getColumnType().getName());
				columnData.setDefaultValue(column.getDefaultValue());
				if(column.getColumnType().supportSize()){
					columnData.setSize(column.getSize());
				}
				if(column.isPrimaryKey()){
					columnData.setPrimaryKey(DBPlugin.getResourceString("label.o"));
				}
				if(column.isNotNull()){
					columnData.setNullable(DBPlugin.getResourceString("label.x"));
				}

				LOOP: for(AbstractDBConnectionModel conn: table.getModelSourceConnections()){
					if(conn instanceof ForeignKeyModel){
						ForeignKeyModel foreignKey = (ForeignKeyModel) conn;
						ForeignKeyMapping[] mappings = foreignKey.getMapping();
						for(ForeignKeyMapping mapping: mappings){
							if(mapping.getRefer() == column){
								columnData.setForeignKey(DBPlugin.getResourceString("label.o"));
								columnData.setReference(
										((TableModel) foreignKey.getTarget()).getTableName()
										+ "." + mapping.getTarget().getColumnName());
								break LOOP;
							}
						}
					}
				}

				columnData.setIndex(columns.size() + 1);
				columns.add(columnData);
			}
			tableData.setColumns(columns);
			tables.add(tableData);
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("tables", tables);

		InputStream in = getClass().getResourceAsStream("template.xls");
		FPTemplate template = new FPTemplate();
		try {
			HSSFWorkbook wb = template.process(in, data);

			FileOutputStream fos = new FileOutputStream(path);
			wb.write(fos);
			IOUtils.close(fos);

		} catch (Exception ex) {
			DBPlugin.logException(ex);
		}
	}

	public String getGeneratorName() {
		return "Excel (for Japanese)";
	}

}
