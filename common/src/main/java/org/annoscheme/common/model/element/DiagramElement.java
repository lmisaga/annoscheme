package org.annoscheme.common.model.element;

import net.sourceforge.plantuml.StringUtils;
import org.annoscheme.common.annotation.ActionType;
import org.annoscheme.common.model.PlantUmlIntegrable;
import org.annoscheme.common.model.UmlParseable;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static org.annoscheme.common.model.constants.PlantUmlConstants.DIAGRAM_END;
import static org.annoscheme.common.model.constants.PlantUmlConstants.DIAGRAM_START;

public class DiagramElement implements PlantUmlIntegrable, UmlParseable, Cloneable {

	private String message;

	private String parentMessage;

	private ActionType actionType = ActionType.ACTION;

	private String[] diagramIdentifiers = new String[]{};

	@Override
	@JsonIgnore
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

	@JsonIgnore
	public String getPlantUmlElementMessage() {
		StringBuilder plantUmlMessage = new StringBuilder("");
		if (StringUtils.isEmpty(message)) {
			return plantUmlMessage.toString();
		}
		plantUmlMessage.append(":").append(trimAndReplaceQuotes(message));
		if (message.endsWith("]")) {
			plantUmlMessage.append("\n");
		} else {
			plantUmlMessage.append(";\n");
		}
		return plantUmlMessage.toString();
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
		this.diagramIdentifiers = diagramIdentifiers != null ? Arrays.stream(diagramIdentifiers)
										.map(this::trimAndReplaceQuotes)
										.toArray(String[]::new) : null;

	}

	public String getParentMessage() {
		return parentMessage != null ? parentMessage: "";
	}

	public void setParentMessage(String parentMessage) {
		this.parentMessage = trimAndReplaceQuotes(parentMessage);
	}

	@Override
	public DiagramElement clone() {
		try {
			return (DiagramElement) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}
