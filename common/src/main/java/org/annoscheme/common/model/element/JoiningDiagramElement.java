package org.annoscheme.common.model.element;

public class JoiningDiagramElement extends ActivityDiagramElement {

	@Override
	public String toPlantUmlString() {
		return this.getPlantUmlElementMessage();
	}

	@Override
	public String getPlantUmlElementMessage() {
		return "endif\n";
	}

	@Override
	public String getMessage() {
		return "join-" + this.getParentMessage();
	}

}
