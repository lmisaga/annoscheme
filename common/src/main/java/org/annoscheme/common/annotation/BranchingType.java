package org.annoscheme.common.annotation;

public enum BranchingType {
	MAIN,
	ALTERNATIVE,
	JOINING;


	public static BranchingType valueOfString(String inputString) {
		if (inputString == null || inputString.isEmpty()) {
			return BranchingType.MAIN;
		}
		return BranchingType.valueOf(inputString);
	}
}


