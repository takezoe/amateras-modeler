package net.java.amateras.db;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	
	private static ResourceBundle resource 
		= ResourceBundle.getBundle("net.java.amateras.db.DBPlugin");
	
	public static String getResourceString(String key){
		try {
			return resource.getString(key);
		} catch(MissingResourceException ex){
			return key;
		}
	}

}
