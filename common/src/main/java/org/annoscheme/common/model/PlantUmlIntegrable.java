package org.annoscheme.common.model;

public interface PlantUmlIntegrable {

	String toPlantUmlString();

	default String getPlantUmlElementMessage() {
		return null;
	};

}
