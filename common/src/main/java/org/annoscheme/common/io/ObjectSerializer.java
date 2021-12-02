package org.annoscheme.common.io;

import org.annoscheme.common.model.ActivityDiagramModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectSerializer {

	private static final String DIAGRAM_CACHE_PATH = "diagram-cache.json";

//	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static void serializeCachedDiagramList(List<ActivityDiagramModel> diagramModels) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.writeValue(new File("diagram-cache.json"), diagramModels);
//			gson.toJson(diagramModels, new FileWriter(DIAGRAM_CACHE_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<ActivityDiagramModel> deserializeCachedDiagramList() {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			objectMapper.addMixIn(ActivityDiagramModel.class, JsonIgnore.class);
			return objectMapper.readValue(new File("diagram-cache.json"), new TypeReference<List<ActivityDiagramModel>>(){});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


}
