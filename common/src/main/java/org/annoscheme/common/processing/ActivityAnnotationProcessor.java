package org.annoscheme.common.processing;

import net.sourceforge.plantuml.SourceStringReader;
import org.annoscheme.common.annotation.ActionType;
import org.annoscheme.common.annotation.BranchingType;
import org.annoscheme.common.io.ObjectSerializer;
import org.annoscheme.common.model.ActivityDiagramModel;
import org.annoscheme.common.model.ConditionalDiagramElement;
import org.annoscheme.common.model.DiagramElement;
import org.annoscheme.common.model.DiagramModelCache;
import org.annoscheme.common.model.constants.AnnotationConstants;
import org.apache.log4j.Logger;

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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import com.google.auto.service.AutoService;

@SupportedAnnotationTypes({"org.annoscheme.common.annotation.Action"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ActivityAnnotationProcessor extends AbstractProcessor {

	private static final Logger logger = Logger.getLogger(ActivityAnnotationProcessor.class);

	private final DiagramModelCache diagramCache = DiagramModelCache.getInstance();

	@Override
	@SuppressWarnings("unchecked")
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!annotations.isEmpty()) {
			for (TypeElement annotation : annotations) {
				Set<ExecutableElement> annotatedElements = (Set<ExecutableElement>) roundEnv.getElementsAnnotatedWith(annotation);
				if (!annotatedElements.isEmpty()) {
					ActivityDiagramModel diagram = new ActivityDiagramModel();
					annotatedElements.forEach(annotatedElement -> {
						//annotated element with @Action might have more of annotation mirrors
						List<? extends AnnotationMirror> annotationMirrors = annotatedElement.getAnnotationMirrors();
						//
						try{
							this.parseDiagramElementsFromAnnotationMirrors(annotationMirrors, diagram);
						} catch (NoSuchElementException ex) {
							ex.printStackTrace();
						}
					});
					diagramCache.addDiagramToCache(diagram);

				}
			}
			this.createDiagrams();
		}
		return true;
	}

	//TODO update to accommodate more than one diagram
	private void parseDiagramElementsFromAnnotationMirrors(List<? extends AnnotationMirror> annotationMirrors, ActivityDiagramModel diagramModel) {
		//if mirrors > 1, then conditional must be present
		if (annotationMirrors.size() == 1) {
			if (annotationMirrors.get(0).getAnnotationType().asElement().getSimpleName().toString().equals(AnnotationConstants.CONDITIONAL_NAME)) {
				throw new IllegalStateException("@Conditional must appear with @Action!");
			}
			DiagramElement elementToAdd = this.parseActivityDiagramElement(annotationMirrors.get(0));
			diagramModel.addElement(elementToAdd);
		} else {
			Optional<? extends AnnotationMirror> actionMirror = annotationMirrors.stream()
																				 .filter(mirror -> mirror.getAnnotationType()
																										 .asElement()
																										 .getSimpleName().toString()
																										 .equals(AnnotationConstants.ACTION_NAME))
																				 .findFirst();
			Optional<? extends AnnotationMirror> conditionalMirror = annotationMirrors.stream()
																					  .filter(mirror -> mirror.getAnnotationType()
																											  .asElement()
																											  .getSimpleName().toString()
																											  .equals(AnnotationConstants.CONDITIONAL_NAME))
																					  .findFirst();
			if (actionMirror.isPresent() && conditionalMirror.isPresent()) {
				DiagramElement elementToAdd = this.parseActivityDiagramElement(actionMirror.get());
				ConditionalDiagramElement conditionalElementToAdd = this.parseConditionalElement(conditionalMirror.get());
				if (BranchingType.MAIN.equals(conditionalElementToAdd.getBranchingType())) {
					conditionalElementToAdd.setMainFlowDirectChild(elementToAdd);
				} else {
					conditionalElementToAdd.setAlternateFlowDirectChild(elementToAdd);
				}
				conditionalElementToAdd.setParentMessage(elementToAdd.getParentMessage());
				diagramModel.addElement(conditionalElementToAdd);
				elementToAdd.setParentMessage(conditionalElementToAdd.getMessage());
				diagramModel.addElement(elementToAdd);
			}

		}
	}

	private DiagramElement parseActivityDiagramElement(AnnotationMirror mirror) {
		DiagramElement element = new DiagramElement();
		mirror.getElementValues().forEach((key, value) -> {
			switch (key.getSimpleName().toString()) {
				case "message":
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

	private ConditionalDiagramElement parseConditionalElement(AnnotationMirror mirror) {
		ConditionalDiagramElement element = new ConditionalDiagramElement();
		mirror.getElementValues().forEach((key, value) -> {
			switch (key.getSimpleName().toString()) {
				case "type":
					element.setBranchingType(this.getBranchingTypeForString(String.valueOf(value.getValue())));
					break;
				case "condition":
					element.setCondition(String.valueOf(value));
					element.setMessage(String.valueOf(value));
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

	private BranchingType getBranchingTypeForString(String inputString) {
		if (inputString == null || inputString.isEmpty()) {
			return BranchingType.MAIN;
		}
		return BranchingType.valueOf(inputString);
	}

	private void createDiagrams() {
		List<String[]> diagramIdentifiers = null;
		ObjectSerializer.serializeCachedDiagramList(DiagramModelCache.getInstance().getActivityDiagrams());
		//TODO remove, test
		List<ActivityDiagramModel> test = ObjectSerializer.deserializeCachedDiagramList();
		logger.info("diagramIdentifiers: " + diagramIdentifiers);
		try {
			//TODO create separate reusable service for writing images
			OutputStream os = new FileOutputStream("img/diagram.png");
			SourceStringReader reader = new SourceStringReader(diagramCache.getActivityDiagrams().get(0).toPlantUmlString());
			logger.info("\n" + diagramCache.getActivityDiagrams().get(0).toPlantUmlString());
			String desc = reader.generateImage(os);
		} catch (IOException | NoSuchElementException e) {
			e.printStackTrace();
		}
	}
}
