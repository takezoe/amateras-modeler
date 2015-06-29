package net.java.amateras.db.dialect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.java.amateras.db.DBPlugin;

public class DialectProvider {
	
	private static Map<String, IDialect> dialects 
		= DBPlugin.getDefault().getContributedDialects();
	
	public static IDialect[] getDialects(){
		Iterator<IDialect> ite = dialects.values().iterator();
		List<IDialect> dialects = new ArrayList<IDialect>();
		while(ite.hasNext()){
			dialects.add(ite.next());
		}
		return dialects.toArray(new IDialect[dialects.size()]);
	}
	
	public static String[] getDialectNames(){
		Set<String> set = dialects.keySet();
		return set.toArray(new String[set.size()]);
	}
	
	public static IDialect getDialect(String dbName){
		return dialects.get(dbName);
	}
	
}
