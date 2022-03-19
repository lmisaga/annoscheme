package org.annoscheme.common.io;

import org.annoscheme.common.model.ActivityDiagramModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

public class DiagramSerializer {

	private static final String DIR_PATH = "output-storage";

	private static final String DIAGRAM_CACHE_PATH = "diagram-cache.json";

	private static final ObjectMapper objectMapper;

	private static final Logger logger = LoggerFactory.getLogger(DiagramSerializer.class);

	static {
		File directory = new File(DIR_PATH);
		if (!directory.exists()) {
			directory.mkdir();
		}
		objectMapper = new ObjectMapper();
		PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
																			  .allowIfSubType("org.annoscheme.common.model")
																			  .allowIfSubType("java.util.ArrayList")
																			  .allowIfSubType("java.util.HashMap")
																			  .build();
		objectMapper.activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.addMixIn(ActivityDiagramModel.class, JsonIgnore.class);
	}

	public static void serializeCachedDiagramsMap(HashMap<String, ActivityDiagramModel> diagramMap) {
		try {
			String diagramMapPath = DIR_PATH + "/" + DIAGRAM_CACHE_PATH;
			logger.info("Serializing cached diagrams map to " + diagramMapPath);
			objectMapper.writeValue(new File(DIR_PATH + "/" + DIAGRAM_CACHE_PATH), diagramMap);
			logger.info("Diagrams successfully serialized to " + diagramMapPath);
		} catch (Exception err) {
			logger.error("Diagrams map could not be serialized: " + err.getMessage());
		}
	}

	public static HashMap<String, ActivityDiagramModel> deserializeCachedDiagramsMap() {
		try {
			logger.info("Deserializing diagrams from " + DIAGRAM_CACHE_PATH);
			return objectMapper.readValue(new File(DIR_PATH + "/" + DIAGRAM_CACHE_PATH), HashMap.class);
		} catch (Exception err) {
			logger.error("Could not deserialize cached diagrams map: " + err.getMessage());
		}
		return new HashMap<>();
	}

}
