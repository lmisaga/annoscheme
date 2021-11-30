package org.annoscheme.common.processing;

import org.annoscheme.common.annotation.ActionType;
import org.annoscheme.common.model.ActivityDiagramModel;
import org.annoscheme.common.model.ConditionalDiagramElement;
import org.annoscheme.common.model.DiagramElement;



import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.auto.service.AutoService;


import net.sourceforge.plantuml.SourceStringReader;
import org.apache.log4j.Logger;

@SupportedAnnotationTypes({"org.annoscheme.common.annotation.Action"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ActivityAnnotationProcessor extends AbstractProcessor {

	private List<DiagramElement> extractedDiagramElements = new ArrayList<>();
	private static final Logger logger = Logger.getLogger(ActivityAnnotationProcessor.class);

	@Override
	@SuppressWarnings("unchecked")
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (TypeElement annotation: annotations) {
			Set<ExecutableElement> annotatedElements = (Set<ExecutableElement>) roundEnv.getElementsAnnotatedWith(annotation);
			if (!annotatedElements.isEmpty()) {

				annotatedElements.forEach(annotatedElement -> {
					//annotated element with @Action might have more of annotation mirrors
					List<? extends AnnotationMirror> annotationMirrors = annotatedElement.getAnnotationMirrors();
					//
					this.extractedDiagramElements.addAll(this.parseDiagramElementsFromAnnotationMirrors(annotationMirrors));
				});
				logger.info("Found" + extractedDiagramElements.size() + "diagram elements");
			}
		}

//		String finalString = sortDiagramElementsByParent(this.extractedDiagramElements);
		System.out.println(this.extractedDiagramElements);
		this.createDiagrams(this.extractedDiagramElements);
		return true;
	}

	private List<DiagramElement> parseDiagramElementsFromAnnotationMirrors(List<? extends AnnotationMirror> annotationMirrors) {
		List<DiagramElement> extractedElements = new ArrayList<>();
		//if mirrors > 1, then conditional must be present
		if (annotationMirrors.size() == 1) {
			extractedElements.add(this.parseActivityDiagramElement(annotationMirrors.get(0)));
		} else {
			//parse conditional and activity diagram element
		}
		return extractedElements;
	}

	private DiagramElement parseActivityDiagramElement(AnnotationMirror mirror) {
		DiagramElement element = new DiagramElement();
		mirror.getElementValues().forEach((key, value) -> {
			switch(key.getSimpleName().toString()) {
				case "message" :
					element.setMessage(String.valueOf(value));
					break;
				case "diagramIdentifiers":
					List<String> identifiers = value.getValue() instanceof List ?
											   (List<String>) value.getValue() :
											   new ArrayList<>();
					if (identifiers.size() == 1) {
						element.setDiagramIdentifiers(new String[]{String.valueOf(identifiers.get(0))});
					} else {
						element.setDiagramIdentifiers(identifiers.stream().map(String::valueOf).toArray(String[]::new));
					}
					break;
				case "parentMessage":
					element.setParentMessage(String.valueOf(value));
					break;
				case "actionType":
					element.setActionType(getActionTypeForElement(String.valueOf(value.getValue())));
					break;
			}
		});

		return element;
	}

	private ActionType getActionTypeForElement(String inputString) {
		//TODO check for IllegalArgumentException?
		if (inputString == null || inputString.isEmpty()) {
			return ActionType.ACTION;
		}
		return ActionType.valueOf(inputString);
	}

	private void createDiagrams(List<DiagramElement> diagramElements) {
		List<String[]> diagramIdentifiers = diagramElements.stream().map(DiagramElement::getDiagramIdentifiers).collect(Collectors.toList());
		System.out.println(diagramIdentifiers);
		ActivityDiagramModel testModel = new ActivityDiagramModel();
		testModel.addElements(this.extractedDiagramElements);
		testModel.setDiagramIdentifier(diagramIdentifiers.get(0)[0]);

		System.out.println(testModel.toPlantUmlString());
		try {
			//TODO create separate reusable service for writing images
			OutputStream os = new FileOutputStream("img/diagram.png");
			SourceStringReader reader = new SourceStringReader(testModel.toPlantUmlString());
			String desc = reader.generateImage(os);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
