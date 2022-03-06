package org.annoscheme.common.model.element;

public class JoiningDiagramElement extends DiagramElement {

	private String mainBranchParentMessage;

	private String altBranchParentMessage;

	//something arbitrary
	private String message;

	@Override
	public String toPlantUmlString() {
		return this.getPlantUmlElementMessage();
	}

	@Override
	public String getPlantUmlElementMessage() {
		return "endif\n";
	}

	@Override
	public String trimAndReplaceQuotes(String inputString) {
		return super.trimAndReplaceQuotes(inputString);
	}

	public String getMainBranchParentMessage() {
		return mainBranchParentMessage;
	}

	public void setMainBranchParentMessage(String mainBranchParentMessage) {
		this.mainBranchParentMessage = mainBranchParentMessage;
	}

	public String getAltBranchParentMessage() {
		return altBranchParentMessage;
	}

	public void setAltBranchParentMessage(String altBranchParentMessage) {
		this.altBranchParentMessage = altBranchParentMessage;
	}

	public String getMessage() {
		return "join" + getMainBranchParentMessage() + "|" + getAltBranchParentMessage();
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
