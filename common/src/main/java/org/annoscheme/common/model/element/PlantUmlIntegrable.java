package org.annoscheme.common.model.element;

public interface PlantUmlIntegrable {

	String toPlantUmlString();

	default String getPlantUmlElementMessage() {
		return null;
	};

}
