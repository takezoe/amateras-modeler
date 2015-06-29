package net.java.amateras.db.dialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.java.amateras.db.util.NameConverter;
import net.java.amateras.db.visual.model.AbstractDBConnectionModel;
import net.java.amateras.db.visual.model.AbstractDBEntityModel;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.ForeignKeyMapping;
import net.java.amateras.db.visual.model.ForeignKeyModel;
import net.java.amateras.db.visual.model.IndexModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A default implementation of {@link ISchemaLoader}.
 * 
 * @author Naoki Takezoe
 */
public class DefaultSchemaLoader implements ISchemaLoader {
	
	public void loadSchema(RootModel root, IDialect dialect, Connection conn, 
			String[] tables, String catalog, String schema, boolean autoConvert) throws SQLException {
		if(tables.length==0){
			return;
		}
		for(int i=0;i<tables.length;i++){
			TableModel table = getTableInfo(tables[i], dialect, conn, catalog, schema, autoConvert);
			
			// merge
			java.util.List<AbstractDBEntityModel> children = root.getChildren();
			for(int j=0;j<children.size();j++){
				AbstractDBEntityModel obj = children.get(j);
				if(obj instanceof TableModel){
					TableModel tableModel = (TableModel) obj;
					if(tableModel.getTableName().equals(table.getTableName())){
						table.setLogicalName(tableModel.getLogicalName());
						table.setDescription(tableModel.getDescription());
						table.setConstraint(tableModel.getConstraint());
						ColumnModel[] columns = table.getColumns();
						for(int k=0;k<columns.length;k++){
							ColumnModel columnModel = tableModel.getColumn(columns[k].getColumnName());
							if(columnModel != null){
								columns[k].setLogicalName(columnModel.getLogicalName());
								columns[k].setDescription(columnModel.getDescription());
							}
						}
						root.removeChild(tableModel);
						break;
					}
				}
			}
			if(table.getConstraint()==null){
				table.setConstraint(new Rectangle(10 + i * 50, 10 + i * 50, -1, -1));
			}
			root.addChild(table);
		}
		
		setForeignKeys(root, conn, catalog, schema);
		
//		// remove indices which defined as foreign keys
//		List<IndexModel> indexModels = new ArrayList<IndexModel>();
//		for(AbstractDBEntityModel entity: root.getChildren()){
//			if(entity instanceof TableModel){
//				TableModel table = ((TableModel) entity);
//				for(IndexModel indexModel: table.getIndices()){
//					boolean match = true;
//					LOOP: for(AbstractDBConnectionModel fkConn: table.getModelSourceConnections()){
//						if(fkConn instanceof ForeignKeyModel){
//							ForeignKeyMapping[] mappings = ((ForeignKeyModel) fkConn).getMapping();
//							if(mappings.length == indexModel.getColumns().size()){
//								for(ForeignKeyMapping mapping: mappings){
//									if(!indexModel.getColumns().contains(mapping.getTarget().getColumnName())){
//										match = false;
//										break LOOP;
//									}
//								}
//							}
//						}
//					}
//					if(!match){
//						indexModels.add(indexModel);
//					}
//				}
//				table.setIndices(indexModels.toArray(new IndexModel[indexModels.size()]));
//			}
//		}
	}
	
	protected TableModel getTableInfo(String tableName, IDialect dialect, 
			Connection conn, String catalog, String schema, boolean autoConvert) throws SQLException {
		
		TableModel table = new TableModel();
		table.setTableName(tableName);
		
		if(autoConvert){
			table.setLogicalName(NameConverter.physical2logical(table.getTableName()));
		} else {
			// TODO Camelize?
			table.setLogicalName(table.getTableName());
		}
		
		DatabaseMetaData meta = conn.getMetaData();
		
		List<ColumnModel> list = new ArrayList<ColumnModel>();
		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(dialect.getColumnMetadataSQL(tableName));
		ResultSetMetaData rm = rs.getMetaData();
		
		ResultSet columns = meta.getColumns(catalog, schema, tableName, "%");
		while(columns.next()){
			IColumnType type = dialect.getColumnType(columns.getString("TYPE_NAME"));
			if(type == null){
				type = dialect.getColumnType(columns.getInt("DATA_TYPE"));
				if(type == null){
					type = dialect.getDefaultColumnType();
				}
			}
			
			ColumnModel column = new ColumnModel();
			column.setColumnName(columns.getString("COLUMN_NAME"));
			if(autoConvert){
				column.setLogicalName(NameConverter.physical2logical(column.getColumnName()));
			} else {
				column.setLogicalName(column.getColumnName());
			}
			column.setColumnType(type);
			column.setSize(columns.getString("COLUMN_SIZE"));
			column.setNotNull(columns.getString("IS_NULLABLE").equals("NO"));
			
			int rmIndex = getResultSetMetaDataIndex(rm, column.getColumnName());
			if(rmIndex > 0){
				column.setAutoIncrement(rm.isAutoIncrement(rmIndex));
			}
			
			list.add(column);
		}
		columns.close();
		
		ResultSet keys = meta.getPrimaryKeys(catalog, schema, tableName);
		while(keys.next()){
			String columnName = keys.getString("COLUMN_NAME");
			for(int i=0;i<list.size();i++){
				ColumnModel column = (ColumnModel)list.get(i);
				if(column.getColumnName().equals(columnName)){
					column.setPrimaryKey(true);
				}
			}
		}
		keys.close();
		
		rs.close();
		stmt.close();
		
		table.setColumns(list.toArray(new ColumnModel[list.size()]));
		
		List<IndexModel> indices = loadIndexModels(tableName, dialect, conn, catalog, schema, list);
		table.setIndices(indices.toArray(new IndexModel[indices.size()]));
		
		return table;
	}
	
	protected List<IndexModel> loadIndexModels(String tableName, IDialect dialect, 
			Connection conn, String catalog, String schema, List<ColumnModel> columns) throws SQLException {
		
		List<IndexModel> result = new ArrayList<IndexModel>();
		DatabaseMetaData meta = conn.getMetaData();
		
		ResultSet rs = meta.getIndexInfo(catalog, schema, tableName, false, true);
		while(rs.next()){
			String indexName = rs.getString("INDEX_NAME");
			if(indexName != null){
				IndexModel indexModel = null;
				for(IndexModel index: result){
					if(index.getIndexName().equals(indexName)){
						indexModel = index;
						break;
					}
				}
				if(indexModel == null){
					indexModel = new IndexModel();
					indexModel.setIndexName(indexName);
					indexModel.setIndexName(rs.getString("INDEX_NAME"));
					if(rs.getBoolean("NON_UNIQUE")){
						indexModel.setIndexType(new IndexType("INDEX"));
					} else {
						indexModel.setIndexType(new IndexType("UNIQUE"));
					}
					result.add(indexModel);
				}
				indexModel.getColumns().add(rs.getString("COLUMN_NAME"));
			}
		}
		rs.close();
		
		List<IndexModel> removeIndexModels = new ArrayList<IndexModel>();
		for(IndexModel indexModel: result){
			List<String> pkColumns = new ArrayList<String>();
			for(ColumnModel columnModel: columns){
				if(columnModel.isPrimaryKey()){
					pkColumns.add(columnModel.getColumnName());
				}
			}
			if(indexModel.getColumns().size() == pkColumns.size()){
				boolean isNotPk = false;
				for(int i=0;i<indexModel.getColumns().size();i++){
					if(!indexModel.getColumns().get(i).equals(pkColumns.get(i))){
						isNotPk = true;
						break;
					}
				}
				if(!isNotPk){
					removeIndexModels.add(indexModel);
				}
			}
		}
		result.removeAll(removeIndexModels);
		
		return result;
	}
	
	/**
	 * Return the index in the <code>ResultSetMetaData</code> for the given column.
	 */
	protected int getResultSetMetaDataIndex(
			ResultSetMetaData rm, String columnName) throws SQLException {
		for(int i=1;i<rm.getColumnCount();i++){
			if(rm.getColumnName(i).equals(columnName)){
				return i;
			}
		}
		return 0;
	}
	
	protected void setForeignKeys(RootModel root, Connection conn,
			String catalog, String schema) throws SQLException {

		for(int i=0;i<root.getChildren().size();i++){
			TableModel table = (TableModel)root.getChildren().get(i);
			for(AbstractDBConnectionModel connModel: table.getModelSourceConnections().toArray(
					new AbstractDBConnectionModel[table.getModelSourceConnections().size()])){
				if(connModel instanceof ForeignKeyModel){
					connModel.detachSource();
					connModel.detachTarget();
				}
			}
		}
		
		DatabaseMetaData meta = conn.getMetaData();
		for(int i=0;i<root.getChildren().size();i++){
			TableModel table = (TableModel)root.getChildren().get(i);
			
			ResultSet rs = meta.getImportedKeys(catalog, schema, table.getTableName());
			Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
			while(rs.next()){
				String pkTable  = rs.getString("PKTABLE_NAME");
				String pkColumn = rs.getString("PKCOLUMN_NAME");
				String fkTable  = rs.getString("FKTABLE_NAME");
				String fkColumn = rs.getString("FKCOLUMN_NAME");
				String keyName  = rs.getString("FK_NAME");
				
				if(root.getTable(pkTable)!=null && root.getTable(fkTable)!=null){
					if(map.get(keyName)==null){
						Map<String, Object> entry = new HashMap<String, Object>();
						entry.put("fkTable", fkTable);
						entry.put("pkTable", pkTable);
						entry.put("mappings", new ArrayList<ForeignKeyMapping>());
						map.put(keyName, entry);
					}
					@SuppressWarnings("unchecked")
					List<ForeignKeyMapping> mappings = 
						(List<ForeignKeyMapping>) map.get(keyName).get("mappings");
					ForeignKeyMapping mapping = new ForeignKeyMapping();
					mapping.setRefer(root.getTable(fkTable).getColumn(fkColumn));
					mapping.setTarget(root.getTable(pkTable).getColumn(pkColumn));
					mappings.add(mapping);
				}
			}
			rs.close();
			
			Iterator<Map.Entry<String, Map<String, Object>>> ite = map.entrySet().iterator();
			while(ite.hasNext()){
				Map.Entry<String, Map<String, Object>> entry = ite.next();
				Map<String, Object> entryMap = entry.getValue();
				
				@SuppressWarnings("unchecked")
				List<ForeignKeyMapping> mappings = 
					(List<ForeignKeyMapping>) entryMap.get("mappings");
				
				ForeignKeyModel fkeyModel = new ForeignKeyModel();
				fkeyModel.setForeignKeyName(entry.getKey());
				fkeyModel.setMapping(mappings.toArray(new ForeignKeyMapping[mappings.size()]));
				
				fkeyModel.setSource(root.getTable((String)entryMap.get("fkTable")));
				fkeyModel.setTarget(root.getTable((String)entryMap.get("pkTable")));
				fkeyModel.attachSource();
				fkeyModel.attachTarget();
			}
		}
	}

}
