package org.annoscheme.common.model.element;

import org.annoscheme.common.annotation.ActionType;
import org.annoscheme.common.annotation.BranchingType;

public class ConditionalActivityDiagramElement extends ActivityDiagramElement {

	private String condition;

	private BranchingType branchingType;

	private ActivityDiagramElement mainFlowDirectChild;

	private ActivityDiagramElement alternateFlowDirectChild;

	public ActionType getActionType() {
		return ActionType.CONDITIONAL;
	}

	public ActivityDiagramElement getMainFlowDirectChild() {
		return mainFlowDirectChild;
	}

	public void setMainFlowDirectChild(ActivityDiagramElement mainFlowDirectChild) {
		this.mainFlowDirectChild = mainFlowDirectChild;
	}

	public ActivityDiagramElement getAlternateFlowDirectChild() {
		return alternateFlowDirectChild;
	}

	public void setAlternateFlowDirectChild(ActivityDiagramElement alternateFlowDirectChild) {
		this.alternateFlowDirectChild = alternateFlowDirectChild;
	}

	public String getCondition() {
		return trimAndReplaceQuotes(condition);
	}

	public void setCondition(String condition) {
		this.condition = trimAndReplaceQuotes(condition);
	}

	public BranchingType getBranchingType() {
		return branchingType;
	}

	public void setBranchingType(BranchingType branchingType) {
		this.branchingType = branchingType;
	}
}
