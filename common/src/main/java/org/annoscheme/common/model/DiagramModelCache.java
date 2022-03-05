package org.annoscheme.common.model;

import org.annoscheme.common.model.element.ActivityDiagramElement;

import java.util.Arrays;
import java.util.HashMap;

public class DiagramModelCache {

	private static DiagramModelCache instance;

	//<key, value> pairs of <diagramIdentifier, diagramModel>
	private final HashMap<String, ActivityDiagramModel> diagramsMap;

	private DiagramModelCache() {
		this.diagramsMap = new HashMap<>();
	}

	public static DiagramModelCache getInstance() {
		if (instance == null) {
			instance = new DiagramModelCache();
		}
		return instance;
	}

	public void addDiagramToCache(ActivityDiagramModel model) {
		String diagramIdentifier = model.getDiagramIdentifier() != null && !model.getDiagramIdentifier().isEmpty() ? model.getDiagramIdentifier() : null;
		if (diagramIdentifier != null) {
			this.diagramsMap.put(diagramIdentifier, model);
		}
	}

	public boolean addElementToDiagramByIdentifier(ActivityDiagramElement elementToAdd) {
		if (elementToAdd.getDiagramIdentifiers().length == 1) {
			this.getModelByDiagramIdentifier(elementToAdd.getDiagramIdentifiers()[0]).addElement(elementToAdd);
			return true;
		} else if (elementToAdd.getDiagramIdentifiers().length > 1) {
			Arrays.stream(elementToAdd.getDiagramIdentifiers())
				  .map(identifier -> {
					  if (this.containsDiagramByIdentifier(identifier)) {
						  this.getModelByDiagramIdentifier(identifier).addElement(elementToAdd);
					  } else {
						  ActivityDiagramModel model = new ActivityDiagramModel();
						  model.setDiagramIdentifier(identifier);
						  model.addElement(elementToAdd);
						  this.diagramsMap.put(identifier, model);
					  }
					  return true;
				  }).close();
		}
		return false;
	}

	public boolean containsDiagramByIdentifier(String diagramIdentifier) {
		return this.diagramsMap.containsKey(diagramIdentifier) && this.diagramsMap.get(diagramIdentifier) != null;
	}

	public ActivityDiagramModel getModelByDiagramIdentifier(String identifier) {
		ActivityDiagramModel model = this.diagramsMap.get(identifier);
		if (model == null) {
			model = new ActivityDiagramModel(identifier);
			this.addDiagramToCache(model);
		}
		return this.diagramsMap.get(identifier);
	}

	public HashMap<String, ActivityDiagramModel> getDiagramsMap() {
		return diagramsMap;
	}

}
