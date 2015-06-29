package net.java.amateras.db.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarClassLoader extends URLClassLoader {
	
	public JarClassLoader(URL url){
		super(new URL[]{url});
	}
	
	public JarClassLoader(URL[] urls){
		super(urls);
	}
	
	public void getJDBCDriverClass(java.util.List<Class<?>> list, Class<?> cls, Class<?> org){
		Class<?>[] interfaces = cls.getInterfaces();
		for(int i=0;i<interfaces.length;i++){
			interfaces[i].getInterfaces();
			if(interfaces[i].equals(Driver.class)){
				list.add(org);
			}
		}
		Class<?> s = cls.getSuperclass();
		if(s != null){
			getJDBCDriverClass(list, s ,org);
		}
	}

	public java.util.List<Class<?>> getJDBCDriverClass(String jarName) throws IOException, ClassNotFoundException {
		if(jarName.equals("")){
			return Collections.emptyList();
		}
		JarFile jarFile = new JarFile(jarName);
		Enumeration<JarEntry> e = jarFile.entries();
		ArrayList<Class<?>> list = new ArrayList<Class<?>>();
		while(e.hasMoreElements()){
			JarEntry entry = (JarEntry)e.nextElement();
			String name = entry.getName();
			if(name.lastIndexOf(".class")!=-1){
				String ccls = name.replaceFirst(".class","").replaceAll("/",".");
				try {
				    Class<?> cls = loadClass(ccls,true);
				    getJDBCDriverClass(list, cls ,cls);
				} catch (NoClassDefFoundError ex) {
				} catch (ClassNotFoundException ex) {
				}
			}
		}
		return list;
	}

}

