package net.java.amateras.db.visual.generate;

public class VelocityUtils {
	
	public String escapeHTML(String str){
		if(str == null){
			return "";
		}
		
		str = str.replaceAll("&", "&amp;");
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		str = str.replaceAll("\"", "&quote;");
		
		str = str.replaceAll("\r\n", "\n");
		str = str.replaceAll("\r", "\n");
		str = str.replaceAll("\n", "<br>");
		
		return str;
	}
	
	public boolean isEmpty(String value){
		return value == null || value.length() == 0;
	}
	
	public String escapeHTML2(String str){
		str = escapeHTML(str);
		if(str.length() == 0){
			return "&nbsp;";
		}
		return str;
	}
	
	public boolean isEmpty(Object[] array){
		return array.length == 0;
	}
	
}
