package net.java.amateras.db.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@SuppressWarnings("rawtypes")
public class GenericsUtils {

	public static Type[] getGenericType(Object obj, Class type) {
		Class cl = obj.getClass();
		while (cl != null) {
			Type t = cl.getGenericSuperclass();
			Type[] res = _getGenericType(type, t);
			if (res != null) {
				return res;
			}
			for (Type t2 : cl.getGenericInterfaces()) {
				res = _getGenericType(type, t2);
				if (res != null) {
					return res;
				}
			}
			cl = cl.getSuperclass();
		}
		return new Type[0];
	}

	private static Type[] _getGenericType(Class type, Type t) {
		if (t != null && t instanceof ParameterizedType
				&& type.equals(((ParameterizedType) t).getRawType())) {
			return type != null ? ((ParameterizedType) t)
					.getActualTypeArguments() : new Type[0];
		}
		return null;
	}
}
