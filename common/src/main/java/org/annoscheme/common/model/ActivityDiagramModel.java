package org.annoscheme.common.model;

import net.sourceforge.plantuml.StringUtils;
import org.annoscheme.common.annotation.ActionType;
import org.annoscheme.common.model.element.ConditionalDiagramElement;
import org.annoscheme.common.model.element.DiagramElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static org.annoscheme.common.model.constants.PlantUmlConstants.END_UML;
import static org.annoscheme.common.model.constants.PlantUmlConstants.START_UML;

public class ActivityDiagramModel implements PlantUmlIntegrable, Cloneable {

	private String diagramIdentifier;

	private List<DiagramElement> diagramElements = new ArrayList<>();

	@Override
	@JsonIgnore
	public String toPlantUmlString() {
		StringBuilder plantUmlStringBuilder = new StringBuilder();
		DiagramElement startElement = diagramElements.stream().filter(x -> x.getActionType().equals(ActionType.START)).findFirst().orElse(null);
		if (startElement == null) {
			throw new IllegalStateException("Diagram has no starting element");
		}
		plantUmlStringBuilder.append(START_UML);
		plantUmlStringBuilder.append(startElement.toPlantUmlString());
		DiagramElement current = startElement;
		boolean done = false;
		while (!done) {
			DiagramElement finalCurrent = current;
			current = diagramElements.stream().filter(x -> x.getParentMessage() != null && x.getParentMessage().equals(finalCurrent.getMessage())).findFirst().get();
			if (current instanceof ConditionalDiagramElement) {
				ConditionalDiagramElement currentConditional = (ConditionalDiagramElement) current;
				plantUmlStringBuilder.append("if (")
									 .append(currentConditional.getCondition())
									 .append(") ")
						.append("then ").append("(true) \n");
				//get main branch
				plantUmlStringBuilder.append(this.getPlantUmlConditionalBranch(currentConditional.getMainFlowDirectChild()));
				plantUmlStringBuilder.append("else (").append("false").append(") \n");
				plantUmlStringBuilder.append(this.getPlantUmlConditionalBranch(currentConditional.getAlternateFlowDirectChild()));
				done = true;
			} else {
				plantUmlStringBuilder.append(current.toPlantUmlString());
			}
			if (current.getActionType().equals(ActionType.END)) {
				done = true;
			}
		}
		plantUmlStringBuilder.append(END_UML);
		return plantUmlStringBuilder.toString();
	}

	private String getPlantUmlConditionalBranch(DiagramElement fromElement) {
		StringBuilder plantUmlStringBuilder = new StringBuilder();
		DiagramElement current = fromElement;
		plantUmlStringBuilder.append(current.toPlantUmlString());
		while (!current.getActionType().equals(ActionType.END)) {
			DiagramElement finalCurrent = current;
			DiagramElement child = diagramElements.stream()
												  .filter(x -> x.getParentMessage() != null &&
															   x.getParentMessage().equalsIgnoreCase(finalCurrent.getMessage().toLowerCase()))
												  .findFirst()
												  .get();
			plantUmlStringBuilder.append(child.toPlantUmlString());
			current = child;
		}
		return plantUmlStringBuilder.toString();
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
			if (StringUtils.isEmpty(element.getParentMessage()) && sortedElements.stream().noneMatch(e -> e.getActionType().equals(ActionType.START))) {
				sortedElements.add(0, element);
				return;
			} else {
				throw new IllegalArgumentException("Diagram already contains start node");
			}
		}

		ConditionalDiagramElement conditionalElement;
		if (element instanceof ConditionalDiagramElement) {
			conditionalElement = (ConditionalDiagramElement) element;
			//lookup if conditional with same condition does not exist already, if yes update with new branch
			Optional<DiagramElement> existingConditionalOptional = sortedElements
					.stream()
					.filter(x -> x instanceof ConditionalDiagramElement &&
								 ((ConditionalDiagramElement) x).getCondition().equalsIgnoreCase(conditionalElement.getCondition().toLowerCase()) &&
								 x.getParentMessage().equalsIgnoreCase(conditionalElement.getParentMessage().toLowerCase()))
					.findFirst();
			//if existing, update it's branches
			if (existingConditionalOptional.isPresent()) {
				//there already is existing conditional with same condition -> update main/alt branch
				ConditionalDiagramElement existingConditional = (ConditionalDiagramElement) existingConditionalOptional.get();
				if (conditionalElement.getAlternateFlowDirectChild() != null && existingConditional.getAlternateFlowDirectChild() == null) {
					existingConditional.setAlternateFlowDirectChild(conditionalElement.getAlternateFlowDirectChild());
				} else {
					existingConditional.setMainFlowDirectChild(conditionalElement.getMainFlowDirectChild());
				}
				return;
			} else {
				//no existing conditional, create new & add branch
			}
		}
		//find parent
		Optional<DiagramElement> parentElement = sortedElements.stream()
															   .filter(x -> x.getMessage().equalsIgnoreCase(element.getParentMessage().toLowerCase()))
															   .findFirst();

		if (parentElement.isPresent()) {
			DiagramElement foundParentElement = parentElement.get();
			sortedElements.add(sortedElements.indexOf(foundParentElement) + 1, element);
		} else {
			sortedElements.add(element);
		}

		setDiagramElements(sortedElements);
	}

	public ActivityDiagramModel() {
	}

	public ActivityDiagramModel(String diagramIdentifier, List<DiagramElement> diagramElements) {
		this.diagramIdentifier = diagramIdentifier;
		this.diagramElements = diagramElements;
	}

	public ActivityDiagramModel(ActivityDiagramModel model) {
		this.diagramElements = model.getDiagramElements();
		this.diagramIdentifier = model.getDiagramIdentifier();
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

	@Override
	public ActivityDiagramModel clone() {
		try {
			return (ActivityDiagramModel) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}
