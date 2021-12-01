package org.annoscheme.common.model;

import java.util.ArrayList;
import java.util.List;

public class DiagramModelCache {

	private static DiagramModelCache instance = null;

	private final List<ActivityDiagramModel> activityDiagrams;

	private DiagramModelCache() {
		this.activityDiagrams = new ArrayList<>();
	}

	public static DiagramModelCache getInstance() {
		if (instance == null) {
			instance = new DiagramModelCache();
		}
		return instance;
	}

	public void addDiagramToCache(ActivityDiagramModel model) {
		this.activityDiagrams.add(model);
	}

	public List<ActivityDiagramModel> getActivityDiagrams() {
		return activityDiagrams.subList(0, activityDiagrams.size());
	}

}
