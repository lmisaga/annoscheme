package org.annoscheme.common.model;

public interface UmlParseable {

	default String trimAndReplaceQuotes(String inputString) {
		return inputString != null ? inputString.replace("\"","").trim() : null;
	}

}
