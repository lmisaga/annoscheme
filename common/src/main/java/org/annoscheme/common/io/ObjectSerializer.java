package org.annoscheme.common.io;

import org.annoscheme.common.model.ActivityDiagramModel;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

public class ObjectSerializer {

	private static final String DIAGRAM_CACHE_PATH = "diagram-cache.json";

	private static final ObjectMapper objectMapper = initializeObjectMapper();

	private static ObjectMapper initializeObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
																			  .allowIfSubType("org.annoscheme.common.model")
																			  .allowIfSubType("java.util.ArrayList")
																			  .build();
		objectMapper.activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.addMixIn(ActivityDiagramModel.class, JsonIgnore.class);
		return objectMapper;

	}

	public static void serializeCachedDiagramList(ActivityDiagramModel diagramModel) {
		try {
			objectMapper.writeValue(new File(DIAGRAM_CACHE_PATH), diagramModel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ActivityDiagramModel deserializeCachedDiagramList() {
		try {
			return objectMapper.readValue(new File("diagram-cache.json"), ActivityDiagramModel.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


}
