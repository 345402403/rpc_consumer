package com.lm.rpc.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerConfigReader {
	
	public static String findProp(String key) {
		Properties p = new Properties();
		try {
			InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("server.properties");
			p.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (String) p.get(key);
	}
}
