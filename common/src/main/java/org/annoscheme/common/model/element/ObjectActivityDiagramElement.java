package org.annoscheme.common.model.element;

public class ObjectActivityDiagramElement extends ActivityDiagramElement {

	public String getPlantUmlElementMessage() {
		StringBuilder plantUmlMessage = new StringBuilder(super.getPlantUmlElementMessage());
		plantUmlMessage.insert(0, "#lightblue");
		plantUmlMessage.deleteCharAt(plantUmlMessage.indexOf(";"));
		return plantUmlMessage.toString();
	}

}
