package org.annoscheme.common.model;

import org.annoscheme.common.annotation.ActionType;

import java.util.Arrays;

import static org.annoscheme.common.model.constants.PlantUmlConstants.*;

public class DiagramElement implements PlantUmlIntegrable, UmlParseable {

	private String message;

	private String parentMessage;

	private ActionType actionType = ActionType.ACTION;

	private String[] diagramIdentifiers;

	@Override
	public String toPlantUmlString() {
		//TODO check for very long messages and split them by certain character count
		StringBuilder plantUmlStringBuilder = new StringBuilder(getPlantUmlElementMessage());
		if (ActionType.START.equals(actionType)) {
			plantUmlStringBuilder.insert(0, DIAGRAM_START);
		} else if (ActionType.END.equals(actionType)) {
			plantUmlStringBuilder.append(DIAGRAM_END);
		}
		return plantUmlStringBuilder.toString();
	}

	public String getPlantUmlElementMessage() {
		return ":" + message.trim() + ";" + " \n";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = trimAndReplaceQuotes(message);
	}

	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	public String[] getDiagramIdentifiers() {
		return diagramIdentifiers;
	}

	public void setDiagramIdentifiers(String[] diagramIdentifiers) {
		this.diagramIdentifiers = Arrays.stream(diagramIdentifiers)
										.map(this::trimAndReplaceQuotes)
										.toArray(String[]::new);

	}

	public String getParentMessage() {
		return parentMessage;
	}

	public void setParentMessage(String parentMessage) {
		this.parentMessage = trimAndReplaceQuotes(parentMessage);
	}
}
