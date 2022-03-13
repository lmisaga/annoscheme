package org.annoscheme.common.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesHandler {

	private static final String PROPERTIES_ROOT_DIR = "properties";
	private static final String PROPERTIES_PATH = PROPERTIES_ROOT_DIR + "/annotationvalue.properties";

	private static PropertiesHandler instance;

	private final Properties annotationProperties;

	private PropertiesHandler() {
		this.annotationProperties = initializeProperties();
	}

	private Properties initializeProperties() {
		try {
			InputStream io = new FileInputStream(PROPERTIES_PATH);
			Properties properties = new Properties();
			properties.load(io);
			return properties;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static PropertiesHandler getInstance() {
		if (instance == null) {
			instance = new PropertiesHandler();
		}
		return instance;
	}

	public String resolvePropertyValue(String key) {
		key = key != null ? key.replace("\"", "").trim() : null;
		return annotationProperties != null ? annotationProperties.getProperty(key) : key;
	}

}
