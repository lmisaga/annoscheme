package org.annoscheme.common.processing;

public class PlantUmlDiagramWriter {

	public void createPlantUmlDiagramPng(String plantUmlString) {


	}

	private boolean validatePlantUmlString(String plantUmlString) {
		return plantUmlString.contains("@startuml") && plantUmlString.contains("@enduml");
	}

}
