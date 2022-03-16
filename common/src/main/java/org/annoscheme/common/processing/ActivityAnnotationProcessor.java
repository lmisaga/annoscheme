package org.annoscheme.common.processing;

import org.annoscheme.common.annotation.ActionType;
import org.annoscheme.common.annotation.BranchingType;
import org.annoscheme.common.io.DiagramSerializer;
import org.annoscheme.common.io.VisualDiagramGenerator;
import org.annoscheme.common.model.DiagramModelCache;
import org.annoscheme.common.model.constants.AnnotationConstants;
import org.annoscheme.common.model.element.ActivityDiagramElement;
import org.annoscheme.common.model.element.ConditionalActivityDiagramElement;
import org.annoscheme.common.model.element.JoiningDiagramElement;
import org.annoscheme.common.properties.PropertiesHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.auto.service.AutoService;

@SupportedAnnotationTypes({"org.annoscheme.common.annotation.Action", "org.annoscheme.common.annotation.Actions"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ActivityAnnotationProcessor extends AbstractProcessor {

	private final DiagramModelCache diagramCache = DiagramModelCache.getInstance();

	private final PropertiesHandler propertiesHandler = PropertiesHandler.getInstance();

	private final Logger logger = LoggerFactory.getLogger(ActivityAnnotationProcessor.class);

	@Override
	@SuppressWarnings("unchecked")
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!annotations.isEmpty()) {
			for (TypeElement annotation : annotations) {
				Set<ExecutableElement> annotatedElements = (Set<ExecutableElement>) roundEnv.getElementsAnnotatedWith(annotation);
				if (!annotatedElements.isEmpty()) {
					annotatedElements.forEach(annotatedElement -> {
						//annotated element with @Action might have more of annotation mirrors
						List<? extends AnnotationMirror> annotationMirrors = annotatedElement.getAnnotationMirrors();
						List<? extends AnnotationMirror> actionsValues = new ArrayList<>();
						if (!annotationMirrors.isEmpty() && annotationMirrors.get(0).getAnnotationType().asElement()
																			 .getSimpleName().toString().equals("Actions")) {
							actionsValues = parseIndividualActions(annotationMirrors.get(0));
						}
						try {
							if (!actionsValues.isEmpty()) {
								this.parseDiagramElementsFromAnnotationMirrors(actionsValues);
							} else {
								this.parseDiagramElementsFromAnnotationMirrors(annotationMirrors);
							}
						} catch (NoSuchElementException ex) {
							logger.error("Exception in ActivityAnnotationProcessor:" + ex);
						}
					});
				}
			}
			this.persistDiagrams();
		}
		return true;
	}

	public List<? extends AnnotationMirror> parseIndividualActions(AnnotationMirror annotation) {
		List<AnnotationMirror> parsedActions = new ArrayList<>();
		for (ExecutableElement executableElement : annotation.getElementValues().keySet()) {
			ArrayList<AnnotationValue> annotValues = new ArrayList<>(
					(Collection<? extends AnnotationValue>) annotation.getElementValues().get(executableElement).getValue());
			parsedActions.addAll(annotValues.stream().map(annotationValue -> (AnnotationMirror) annotationValue.getValue()).collect(Collectors.toList()));

		}
		return parsedActions;
	}

	private void parseDiagramElementsFromAnnotationMirrors(List<? extends AnnotationMirror> annotationMirrors) {
		annotationMirrors = filterMirrorsForActionAnnotations(annotationMirrors);
		// check if annotation mirrors size == 1 and whether it contains something other than @Action
		if (annotationMirrors.size() == 1) {
			if (!annotationMirrors.get(0).getAnnotationType().asElement().getSimpleName().toString()
								  .equals(AnnotationConstants.ACTION_NAME)) {
				throw new IllegalStateException("@Conditional or @Joining must appear with @Action!");
			}
			//TODO check if mirrors do not contain @Joining and @Conditional at the same time
		}
		List<? extends AnnotationMirror> actionMirrors = annotationMirrors
				.stream()
				.filter(mirror -> mirror.getAnnotationType()
										.asElement()
										.getSimpleName().toString()
										.equals(AnnotationConstants.ACTION_NAME))
				.collect(Collectors.toList());
		Optional<? extends AnnotationMirror> conditionalMirror = annotationMirrors
				.stream()
				.filter(mirror -> mirror.getAnnotationType()
										.asElement()
										.getSimpleName().toString()
										.equals(AnnotationConstants.CONDITIONAL_NAME))
				.findFirst();
		Optional<? extends AnnotationMirror> joiningMirror = annotationMirrors
				.stream()
				.filter(mirror -> mirror.getAnnotationType()
										.asElement()
										.getSimpleName().toString()
										.equals(AnnotationConstants.JOINING_NAME))
				.findFirst();
		if (!actionMirrors.isEmpty()) {
			List<ActivityDiagramElement> actionElementsToAdd = actionMirrors.stream().map(this::parseActivityDiagramElement).collect(Collectors.toList());
			if (joiningMirror.isPresent()) {
				if (conditionalMirror.isPresent()) {
					throw new IllegalStateException("@Conditional and @Joining must not be used simultaneously!");
				}
				JoiningDiagramElement joiningElementToAdd = this.parseJoiningElement(joiningMirror.get());
				this.diagramCache.addElementToDiagramByIdentifier(joiningElementToAdd);
				//set actionElementsToAdd parent message to joining.message
				actionElementsToAdd.forEach(elementToAdd -> elementToAdd.setParentMessage(joiningElementToAdd.getMessage()));
			}
			if (conditionalMirror.isPresent()) {
				ActivityDiagramElement elementToAdd;
				ConditionalActivityDiagramElement conditionalElementToAdd = this.parseConditionalElement(conditionalMirror.get());
				if (actionElementsToAdd.stream().anyMatch(element -> Arrays.equals(element.getDiagramIdentifiers(),
																				   conditionalElementToAdd.getDiagramIdentifiers()))) {
					//TODO check this
					elementToAdd = actionElementsToAdd.stream()
													  .filter(element -> Arrays.equals(element.getDiagramIdentifiers(),
																					   conditionalElementToAdd.getDiagramIdentifiers())).findFirst().get();
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
					actionElementsToAdd.remove(elementToAdd);
				}
			}
			actionElementsToAdd.forEach(this.diagramCache::addElementToDiagramByIdentifier);
		}

	}

	private List<? extends AnnotationMirror> filterMirrorsForActionAnnotations(List<? extends AnnotationMirror> annotationMirrors) {
		List<String> allowedAnnotationNames = Arrays.asList(AnnotationConstants.CONDITIONAL_NAME,
															AnnotationConstants.ACTION_NAME,
															AnnotationConstants.JOINING_NAME);
		return annotationMirrors.stream()
								.filter(mirror -> {
									String annotationName = mirror.getAnnotationType().asElement().getSimpleName().toString();
									annotationName = annotationName != null ? annotationName : "";
									return allowedAnnotationNames.contains(annotationName);
								}).collect(Collectors.toList());
	}

	private ActivityDiagramElement parseActivityDiagramElement(AnnotationMirror mirror) {
		ActivityDiagramElement element = new ActivityDiagramElement();
		mirror.getElementValues().forEach((key, value) -> {
			switch (key.getSimpleName().toString()) {
				case "message":
					element.setMessage(this.resolvePropertyValue(String.valueOf(value)));
					break;
				case "diagramIdentifiers":
					element.setDiagramIdentifiers(parseDiagramIdentifiers(value));
					break;
				case "parentMessage":
					element.setParentMessage(this.resolvePropertyValue(String.valueOf(value)));
					break;
				case "actionType":
					element.setActionType(getActionTypeForElement(String.valueOf(value.getValue())));
					break;
			}
		});

		return element;
	}

	private JoiningDiagramElement parseJoiningElement(AnnotationMirror mirror) {
		JoiningDiagramElement element = new JoiningDiagramElement();
		mirror.getElementValues().forEach((key, value) -> {
			switch (key.getSimpleName().toString()) {
				case "condition":
					element.setParentMessage(this.resolvePropertyValue(String.valueOf(value)));
					break;
				case "diagramIdentifiers":
					element.setDiagramIdentifiers(parseDiagramIdentifiers(value));
					break;
			}
		});
		return element;
	}

	private ConditionalActivityDiagramElement parseConditionalElement(AnnotationMirror mirror) {
		ConditionalActivityDiagramElement element = new ConditionalActivityDiagramElement();
		mirror.getElementValues().forEach((key, value) -> {
			switch (key.getSimpleName().toString()) {
				case "type":
					element.setBranchingType(BranchingType.valueOfString(String.valueOf(value.getValue())));
					break;
				case "condition":
					element.setCondition(this.resolvePropertyValue(String.valueOf(value)));
					element.setMessage(element.getCondition());
					break;
				case "diagramIdentifiers":
					element.setDiagramIdentifiers(parseDiagramIdentifiers(value));
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

	private String[] parseDiagramIdentifiers(AnnotationValue identifiersAnnotationValue) {
		List<String> identifiers = identifiersAnnotationValue.getValue() instanceof List ?
								   (List<String>) identifiersAnnotationValue.getValue() :
								   new ArrayList<>();
		if (identifiers.size() == 1) {
			return new String[]{this.resolvePropertyValue(String.valueOf(identifiers.get(0)))};
		} else {
			return identifiers.stream().map(String::valueOf)
							  .map(this::resolvePropertyValue)
							  .toArray(String[]::new);
		}
	}

	private void persistDiagrams() {
		DiagramSerializer.serializeCachedDiagramsMap(this.diagramCache.getDiagramsMap());
		diagramCache.getDiagramsMap().forEach((key, value) -> VisualDiagramGenerator.generateImageFromPlantUmlString(value.toPlantUmlString(), key));
	}

	private String resolvePropertyValue(String key) {
		return this.propertiesHandler.resolvePropertyValue(key);
	}
}
