package org.annoscheme.common.processing;

import org.annoscheme.common.annotation.ActionType;
import org.annoscheme.common.annotation.BranchingType;
import org.annoscheme.common.io.ObjectSerializer;
import org.annoscheme.common.io.VisualDiagramGenerator;
import org.annoscheme.common.model.ActivityDiagramModel;
import org.annoscheme.common.model.DiagramModelCache;
import org.annoscheme.common.model.constants.AnnotationConstants;
import org.annoscheme.common.model.element.ConditionalDiagramElement;
import org.annoscheme.common.model.element.DiagramElement;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.auto.service.AutoService;

@SupportedAnnotationTypes({"org.annoscheme.common.annotation.Action"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ActivityAnnotationProcessor extends AbstractProcessor {

	private static final String PROPERTIES_PATH = "/annotationvalue.properties";

	private static final Logger logger = Logger.getLogger(ActivityAnnotationProcessor.class);
	private static final Properties properties = initProperties();

	private final DiagramModelCache diagramCache = DiagramModelCache.getInstance();

	private static Properties initProperties() {
		try (InputStream input = ActivityAnnotationProcessor.class.getResourceAsStream(PROPERTIES_PATH)) {
			Properties properties = new Properties();
			properties.load(input);
			System.out.println(properties);
			return properties;

		} catch (Exception io) {
			logger.error("Properties could not be initialized due to " + io.getMessage() + " -> Defaulting to string annotation value definitions");
			io.printStackTrace();
		}
		return null;
	}

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
						try {
							this.parseDiagramElementsFromAnnotationMirrors(annotationMirrors);
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
	private void parseDiagramElementsFromAnnotationMirrors(List<? extends AnnotationMirror> annotationMirrors) {
		ActivityDiagramModel diagramModel = new ActivityDiagramModel();
		annotationMirrors = filterMirrorsForActionAnnotations(annotationMirrors);
		//if mirrors > 1, then conditional must be present
		if (annotationMirrors.size() == 1) {
			if (annotationMirrors.get(0).getAnnotationType().asElement().getSimpleName().toString().equals(AnnotationConstants.CONDITIONAL_NAME)) {
				throw new IllegalStateException("@Conditional must appear with @Action!");
			}
			DiagramElement elementToAdd = this.parseActivityDiagramElement(annotationMirrors.get(0));
			this.diagramCache.addElementToDiagramByIdentifier(elementToAdd);
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
				conditionalElementToAdd.setDiagramIdentifiers(elementToAdd.getDiagramIdentifiers());
				this.diagramCache.addElementToDiagramByIdentifier(conditionalElementToAdd);
				elementToAdd.setParentMessage(conditionalElementToAdd.getMessage());
				this.diagramCache.addElementToDiagramByIdentifier(elementToAdd);
			}

		}
	}

	private List<? extends AnnotationMirror> filterMirrorsForActionAnnotations(List<? extends AnnotationMirror> annotationMirrors) {
		return annotationMirrors.stream()
								.filter(mirror -> {
									String annotationName = mirror.getAnnotationType().asElement().getSimpleName().toString();
									annotationName = annotationName != null ? annotationName : "";
									return annotationName.equals(AnnotationConstants.CONDITIONAL_NAME) ||
										   annotationName.equals(AnnotationConstants.ACTION_NAME);
								}).collect(Collectors.toList());
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
		String[] diagramIdentifiers = this.diagramCache.getDiagramsMap().keySet().toArray(new String[0]);
//		ActivityDiagramModel model = DiagramModelCache.getInstance().getActivityDiagrams().get(0);
		ObjectSerializer.serializeCachedDiagramsMap(this.diagramCache.getDiagramsMap());
		//TODO remove, test
		HashMap<String, ActivityDiagramModel> test = ObjectSerializer.deserializeCachedDiagramsMap();
		VisualDiagramGenerator.generateImageFromPlantUmlString(test.get(diagramIdentifiers[0]).toPlantUmlString(), "1");
	}
}
