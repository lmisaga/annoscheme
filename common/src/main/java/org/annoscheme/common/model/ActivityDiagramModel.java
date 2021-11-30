package org.annoscheme.common.model;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

public class ActivityDiagramModel implements PlantUmlIntegrable {

	private String diagramIdentifier;

	private List<DiagramElement> diagramElements = new ArrayList<>();

	@Override
	public String toPlantUmlString() {
		StringBuilder plantUmlStringBuilder = new StringBuilder();
		plantUmlStringBuilder.append("@startuml \n");
		if (!diagramElements.isEmpty()) {
			diagramElements.stream()
						   .map(DiagramElement::toPlantUmlString)
						   .forEach(plantUmlStringBuilder::append);
		}
		plantUmlStringBuilder.append("@enduml \n");
		return plantUmlStringBuilder.toString();
	}

	public void addElements(List<DiagramElement> elements) {
		if (this.diagramElements == null) {
			this.diagramElements = new ArrayList<>();
		}
		this.diagramElements.addAll(elements);
	}

	public void addElement(DiagramElement element) {
		addElements(Collections.singletonList(element));
	}

	public String getDiagramIdentifier() {
		return diagramIdentifier;
	}

	public void setDiagramIdentifier(String diagramIdentifier) {
		this.diagramIdentifier = diagramIdentifier;
	}

	public List<DiagramElement> getDiagramElements() {
		return diagramElements;
	}

	public void setDiagramElements(List<DiagramElement> diagramElements) {
		this.diagramElements = diagramElements;
	}
}
