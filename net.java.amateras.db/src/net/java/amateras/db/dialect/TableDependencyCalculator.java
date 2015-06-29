package net.java.amateras.db.dialect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.java.amateras.db.visual.model.AbstractDBConnectionModel;
import net.java.amateras.db.visual.model.ForeignKeyModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

/**
 *
 * @author Naoki Takezoe
 */
public class TableDependencyCalculator {

	public static List<TableModel> getSortedTable(RootModel root){
		List<TableModel> result = new ArrayList<TableModel>();
		for(TableModel table: root.getTables()){
			addTableModel(result, table, null);
		}
		return result;
	}

	private static void addTableModel(List<TableModel> result, TableModel table, Set<TableModel> dependentModels) {

		// Dependent models provides circular reference protection. Contains all models up the recursion stack
		if (dependentModels == null) {
			dependentModels = new HashSet<TableModel>();
		}

		// We might have been already added as dependency by earlier tables
		if (result.contains(table)) {
			return;
		}

		// First add my dependencies
		Set<TableModel> innerDependentModels = new HashSet<TableModel>(dependentModels);
		dependentModels.add(table);

		for(AbstractDBConnectionModel conn: table.getModelSourceConnections()){
			if(conn instanceof ForeignKeyModel) {
				ForeignKeyModel fk = (ForeignKeyModel)conn;
				TableModel target = (TableModel) fk.getTarget();
				if (!dependentModels.contains(target)) {
					addTableModel(result, target, innerDependentModels);
				}
//				else {
//					throw new IllegalStateException("Circular foreign key dependency between tables " + table.getTableName() + " and " + target.getTableName());
//				}
			}
		}

		// Then add myself
		result.add(table);
	}

}
