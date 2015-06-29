package net.java.amateras.db.util;

import net.java.amateras.db.visual.model.AbstractDBConnectionModel;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.ForeignKeyMapping;
import net.java.amateras.db.visual.model.ForeignKeyModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

/**
 * Provides utilities for model operating.
 *
 * @author Naoki Takezoe
 */
public class ModelUtils {

	public static void importOrReplaceTable(RootModel rootModel, TableModel oldTable, TableModel newTable){
		stripConnections(newTable.getModelSourceConnections());
		stripConnections(newTable.getModelTargetConnections());

		if (oldTable != null) {
			rootModel.removeChild(oldTable);
			for (AbstractDBConnectionModel conn: oldTable.getModelSourceConnections()){
				if(conn instanceof ForeignKeyModel){
					ForeignKeyModel fk = (ForeignKeyModel) conn;
					ForeignKeyMapping[] mappings = fk.getMapping();
					for(ForeignKeyMapping mapping: mappings){
						ColumnModel oldColumn = mapping.getRefer();
						ColumnModel newColumn = newTable.getColumn(oldColumn.getColumnName());
						mapping.setRefer(newColumn);
					}
					fk.setMapping(mappings);
				}
				conn.setSource(newTable);
				newTable.addSourceConnection(conn);
			}

			for (AbstractDBConnectionModel conn : oldTable.getModelTargetConnections()) {
				if(conn instanceof ForeignKeyModel){
					ForeignKeyModel fk = (ForeignKeyModel) conn;
					ForeignKeyMapping[] mappings = fk.getMapping();
					for(ForeignKeyMapping mapping: mappings){
						ColumnModel oldColumn = mapping.getTarget();
						ColumnModel newColumn = newTable.getColumn(oldColumn.getColumnName());
						mapping.setTarget(newColumn);
					}
					fk.setMapping(mappings);
				}
				conn.setTarget(newTable);
				newTable.addTargetConnection(conn);
			}
			newTable.setConstraint(oldTable.getConstraint());
			newTable.setLinkedPath(oldTable.getLinkedPath());
		}

		rootModel.addChild(newTable);
	}

	public static void stripConnections(java.util.List<AbstractDBConnectionModel> conns){
		for(AbstractDBConnectionModel conn: conns.toArray(new AbstractDBConnectionModel[conns.size()])){
			conn.getSource().removeSourceConnection(conn);
			conn.getTarget().removeTargetConnection(conn);
		}
	}


}
