package org.annoscheme.aspects;

import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.io.ObjectSerializer;
import org.annoscheme.common.io.VisualDiagramGenerator;
import org.annoscheme.common.model.ActivityDiagramModel;
import org.annoscheme.common.model.element.DiagramElement;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Aspect
public class AnnotationInterceptor {

	private static final Logger logger = Logger.getLogger(AnnotationInterceptor.class);

	private List<ActivityDiagramModel> diagramModels = Collections.singletonList(ObjectSerializer.deserializeCachedDiagramList());

	private Integer executionCount = 1;

	@Around("execution(* *(..)) && @annotation(actionAnnotation)")
	public Object annotation(ProceedingJoinPoint joinPoint, Action actionAnnotation) throws Throwable {
		ActivityDiagramModel currentDiagram = diagramModels.get(0);
		Object joinPointResult = joinPoint.proceed();
		if (joinPointResult == null) {
			return null;
		}
		logger.info(actionAnnotation);
		this.createObjectAndGenerateDiagram(new ActivityDiagramModel(currentDiagram), joinPointResult, actionAnnotation);
		return joinPointResult;
	}

	private void createObjectAndGenerateDiagram(ActivityDiagramModel currentDiagram, Object joinPointResult, Action actionAnnotation) throws Throwable {
		DiagramElement objectElement = createObjectDiagramElement(joinPointResult);
		Optional<DiagramElement> element = null;
		Optional<DiagramElement> successorOptional = null;
		element = currentDiagram.getDiagramElements().stream().filter(x -> x.getMessage().equals(actionAnnotation.message())).findFirst();
		if (element.isPresent()) {
			DiagramElement currentElement = element.get();
			successorOptional = currentDiagram.getDiagramElements().stream().filter(x -> x.getParentMessage().equals(currentElement.getMessage())).findFirst();
			if (successorOptional.isPresent()) {
				objectElement.setParentMessage(currentElement.getMessage());
				DiagramElement successorElement = successorOptional.get();
				successorElement.setParentMessage(objectElement.getMessage());
				currentDiagram.addElement(objectElement);
				objectElement.setDiagramIdentifiers(currentElement.getDiagramIdentifiers());
				//successor is present, that means object element is going to be placed within the diagram

			} else {
				logger.info("successor le missing :)");
				//there is no successor, which means that currentElement should be ActionType.END -> ActionType needs to be altered, along with
			}
			VisualDiagramGenerator.generateImageFromPlantUmlString(currentDiagram.toPlantUmlString(), "test"+executionCount++);
		}
		// get successor and current element
		// create object diagram
		//append object diagram to the diagram after element but before predecessor
		//generate activity diagram with this object
		//done

	}

	private DiagramElement createObjectDiagramElement(Object joinPointResult) throws Throwable {
		DiagramElement objectElement = new DiagramElement();
		StringBuilder objectMessageBuilder = new StringBuilder("<b>" + joinPointResult.getClass().getSimpleName() + "</b>\n");
		Field[] fields = joinPointResult.getClass().getDeclaredFields();
		AccessibleObject.setAccessible(fields, true);
		for (Field field : fields) {
			if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
				objectMessageBuilder.append(field.getName())
									.append(": ")
									.append(field.getType().getSimpleName()).append(" = ").append(field.get(joinPointResult))
									.append("\n");
			}
		}
		objectMessageBuilder.insert(objectMessageBuilder.length() - 1, "]");
		objectElement.setMessage(objectMessageBuilder.toString().trim());
		return objectElement;
	}
}
