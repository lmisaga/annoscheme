package org.annoscheme.common.io;

import net.sourceforge.plantuml.SourceStringReader;
import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.NoSuchElementException;

public class VisualDiagramGenerator {

	private static final Logger logger = Logger.getLogger(VisualDiagramGenerator.class);

	public static void generateImageFromPlantUmlString(String plantUmlString, String fileName) {
		try {
			String imageFileName = "img/" + fileName + ".png";
			OutputStream os = new FileOutputStream(imageFileName);
			SourceStringReader reader = new SourceStringReader(plantUmlString);
			logger.info("Saving diagram image " + fileName);
			reader.generateImage(os);
		} catch (IOException | NoSuchElementException e) {
			logger.error("Error while generating image: " + e.getMessage());
		}
	}

}
