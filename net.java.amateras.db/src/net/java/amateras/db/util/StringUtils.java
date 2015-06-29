package net.java.amateras.db.util;

/**
 *
 * @author Naoki Takezoe
 */
public class StringUtils {

	public static boolean isEmpty(String value){
		return value == null || value.length() == 0;
	}

	public static boolean isNotEmpty(String value){
		return !isEmpty(value);
	}

    public static String rpad(String s, int i){
        char ac[] = new char[i - s.length()];

        for (int j = 0; j < ac.length; j++){
            ac[j] = ' ';
        }

        return s +  String.valueOf(ac);
    }
}
