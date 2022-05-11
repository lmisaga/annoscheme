package org.annoscheme.common.io;

import net.sourceforge.plantuml.SourceStringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.NoSuchElementException;

public class VisualDiagramGenerator {

	private static final Logger logger = LogManager.getLogger(VisualDiagramGenerator.class);

	private static final String IMG_DIR_PATH = "img";

	public static void generateImageFromPlantUmlString(String plantUmlString, String fileName) {
		try {
			File directory = new File(IMG_DIR_PATH);
			if (!directory.exists()) {
				directory.mkdir();
			}
			String imageFileName = "img/" + fileName + ".png";
			OutputStream os = new FileOutputStream(imageFileName);
			SourceStringReader reader = new SourceStringReader(plantUmlString);
			StringBuilder logMessageBuilder = new StringBuilder("Saving diagram ")
					.append(fileName)
					.append(plantUmlString);
			logger.info(logMessageBuilder);
			reader.generateImage(os);
		} catch (IOException | NoSuchElementException e) {
			logger.error("Error while generating image: " + e.getMessage());
		}
	}

}
