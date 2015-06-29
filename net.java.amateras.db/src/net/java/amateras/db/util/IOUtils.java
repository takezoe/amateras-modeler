package net.java.amateras.db.util;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
	
	public static void close(Closeable closeable){
		if(closeable != null){
			try {
				closeable.close();
			} catch(Exception ex){
			}
		}
	}
	
	public static void copyStream(InputStream in, OutputStream out){
		try {
			byte[] buf = new byte[in.available()];
			in.read(buf);
			out.write(buf);
		} catch(Exception ex){
			throw new RuntimeException(ex);
		} finally {
			close(in);
			close(out);
		}
	}
	
	public static String loadStream(InputStream in, String charset){
		try {
			byte[] buf = new byte[in.available()];
			in.read(buf);
			return new String(buf, charset);
		} catch(Exception ex){
			throw new RuntimeException(ex);
		} finally {
			IOUtils.close(in);
		}
	}
	
	
}
