package net.java.amateras.uml.classdiagram.model;

import java.io.Serializable;

public abstract class Visibility implements Serializable {
	
	/**
	 * Don't create an instanse.
	 */
	private Visibility(){
	}
	
	public static final Visibility PUBLIC = new Visibility(){
		public String toString(){
			return "public";
		}
	};
	
	public static final Visibility PRIVATE = new Visibility(){
		public String toString(){
			return "private";
		}
	};
	
	public static final Visibility PROTECTED = new Visibility(){
		public String toString(){
			return "protected";
		}
	};
	
	public static final Visibility PACKAGE = new Visibility(){
		public String toString(){
			return "package";
		}
	};
	
	public boolean equals(Object obj){
		if(obj instanceof Visibility && obj.toString().equals(toString())){
			return true;
		}
		return false;
	}
	
	public static Visibility[] getVisibilities(){
		return new Visibility[]{
				PUBLIC,PRIVATE,PROTECTED,PACKAGE
		};
	}
}
