package org.annoscheme.common.io;

import org.annoscheme.common.model.ActivityDiagramModel;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

public class DiagramSerializer {

	private static final String DIR_PATH = "/output-storage";

	private static final String DIAGRAM_CACHE_PATH = "diagram-cache.json";

	private static final ObjectMapper objectMapper = initializeObjectMapper();

	private static final Logger logger = Logger.getLogger(VisualDiagramGenerator.class);

	static {
		File directory = new File(DIR_PATH);
		if (!directory.exists()) {
			directory.mkdir();
		}
	}

	private static ObjectMapper initializeObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
																			  .allowIfSubType("org.annoscheme.common.model")
																			  .allowIfSubType("java.util.ArrayList")
																			  .allowIfSubType("java.util.HashMap")
																			  .build();
		objectMapper.activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.addMixIn(ActivityDiagramModel.class, JsonIgnore.class);
		return objectMapper;

	}

	public static void serializeCachedDiagramsMap(HashMap<String, ActivityDiagramModel> diagramMap) {
		try {
			logger.info("Serializing cached diagrams map to " + DIAGRAM_CACHE_PATH);
			objectMapper.writeValue(new File(DIAGRAM_CACHE_PATH), diagramMap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static HashMap<String, ActivityDiagramModel> deserializeCachedDiagramsMap() {
		try {
			logger.info("Deserializing diagrams from " + DIAGRAM_CACHE_PATH);
			return objectMapper.readValue(new File(DIAGRAM_CACHE_PATH), HashMap.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
