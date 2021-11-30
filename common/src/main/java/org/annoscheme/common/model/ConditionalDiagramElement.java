package org.annoscheme.common.model;

import org.annoscheme.common.annotation.BranchingType;

public class ConditionalDiagramElement extends DiagramElement {

	private String condition;

	private BranchingType branchingType;

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public BranchingType getBranchingType() {
		return branchingType;
	}

	public void setBranchingType(BranchingType branchingType) {
		this.branchingType = branchingType;
	}
}
