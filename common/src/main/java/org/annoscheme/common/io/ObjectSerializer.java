package org.annoscheme.common.io;

import org.annoscheme.common.model.ActivityDiagramModel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

public class ObjectSerializer {

	private static final String DIR_PATH = "/output-storage";

	private static final String DIAGRAM_CACHE_PATH = "diagram-cache.json";

	private static final ObjectMapper objectMapper = initializeObjectMapper();

	static {
		File directory = new File(DIR_PATH);
		if (! directory.exists()){
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
			objectMapper.writeValue(new File(DIAGRAM_CACHE_PATH), diagramMap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void serializeCachedDiagramsToPlantUml(HashMap<String, ActivityDiagramModel> diagramMap) {
		String str = "Hello";
		//			FileOutputStream outputStream = new FileOutputStream(fileName);
		byte[] strToBytes = str.getBytes();
		//			outputStream.write(strToBytes);

		//			outputStream.close();
	}

	public static HashMap<String, ActivityDiagramModel> deserializeCachedDiagramsMap() {
		try {
			return objectMapper.readValue(new File("diagram-cache.json"), HashMap.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


}
