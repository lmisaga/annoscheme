package org.annoscheme.common.model;

import org.annoscheme.common.annotation.ActionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
		List<DiagramElement> sortedElements = this.getDiagramElements();
		//find parent if not empty, then indexOf parent -> add after parent
		if (sortedElements.isEmpty()) {
			element.setParentElement(null);
			sortedElements.add(element);
			return;
		}
		if (ActionType.START.equals(element.getActionType())) {
			if (sortedElements.stream().noneMatch(e -> e.getActionType().equals(ActionType.START))) {
				sortedElements.add(0, element);
				return;
			} else {
				throw new IllegalArgumentException("Diagram already contains start node");
			}
		}
		if (element instanceof ConditionalDiagramElement) {
			ConditionalDiagramElement conditionalElement = (ConditionalDiagramElement) element;
			//lookup if not already exist
			Optional<DiagramElement> existingConditionalOptional = sortedElements
					.stream()
					.filter(x -> x instanceof ConditionalDiagramElement &&
								 ((ConditionalDiagramElement) x).getCondition().equalsIgnoreCase(conditionalElement.getCondition().toLowerCase()) &&
								 x.getParentMessage().equalsIgnoreCase(conditionalElement.getParentMessage().toLowerCase()))
					.findFirst();
			//if existing, update it's branches
			if (existingConditionalOptional.isPresent()) {
				ConditionalDiagramElement existingConditional = (ConditionalDiagramElement) existingConditionalOptional.get();
				if (conditionalElement.getAlternateFlowDirectChild() != null && existingConditional.getAlternateFlowDirectChild() == null) {
					existingConditional.setAlternateFlowDirectChild(conditionalElement.getAlternateFlowDirectChild());
				} else {
					existingConditional.setMainFlowDirectChild(conditionalElement.getMainFlowDirectChild());
				}
			}
		}
		//find parent
		Optional<DiagramElement> parentElement = sortedElements.stream()
															   .filter(x -> x.getMessage().equalsIgnoreCase(element.getParentMessage().toLowerCase()))
															   .findFirst();

		parentElement.ifPresent(foundParentElement -> {
			element.setParentElement(foundParentElement);
			sortedElements.add(sortedElements.indexOf(foundParentElement) + 1, element);
		});


		setDiagramElements(sortedElements);
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
