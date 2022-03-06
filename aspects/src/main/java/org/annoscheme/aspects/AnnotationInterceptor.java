package org.annoscheme.aspects;

import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.ActionType;
import org.annoscheme.common.annotation.Actions;
import org.annoscheme.common.io.DiagramSerializer;
import org.annoscheme.common.io.VisualDiagramGenerator;
import org.annoscheme.common.model.ActivityDiagramModel;
import org.annoscheme.common.model.element.ActivityDiagramElement;
import org.annoscheme.common.model.element.ObjectActivityDiagramElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.ConstructorSignature;

import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;

@Aspect
public class AnnotationInterceptor {

	private static final String PROPERTIES_PATH = "/annotationvalue.properties";

	private static final Logger logger = LogManager.getLogger(AnnotationInterceptor.class);

	private HashMap<String, ActivityDiagramModel> diagramsMap = DiagramSerializer.deserializeCachedDiagramsMap();

	private static final Properties properties = initProperties();

	private static Properties initProperties() {
		try (InputStream input = AnnotationInterceptor.class.getResourceAsStream(PROPERTIES_PATH)) {
			Properties properties = new Properties();
			properties.load(input);
			return properties;

		} catch (Exception io) {
			logger.error("Properties could not be initialized due to " + io.getMessage() + " -> Defaulting to string annotation value definitions");
			io.printStackTrace();
		}
		return null;
	}

	private Integer executionCount = 1;

	@Around("(execution(* *(..)) || execution(*.new(..))) && @annotation(actionAnnotation)")
	public Object actionAnnotationAdvice(ProceedingJoinPoint joinPoint, Action actionAnnotation) throws Throwable {
		String resolvedIdentifier = resolvePropertyValue(actionAnnotation.diagramIdentifiers()[0]);
		ActivityDiagramModel currentDiagram = diagramsMap.get(resolvedIdentifier).clone();

		logger.debug(actionAnnotation);
		Object joinPointResult = joinPoint.proceed();
		if (joinPoint.getKind().contains("constructor")) { // joinpoint is a constructor call
			this.createObjectAndGenerateDiagramFromJoinPoint(new ActivityDiagramModel(currentDiagram), actionAnnotation, joinPoint);
		}
		if (joinPointResult != null) { // joinpoint is a method call
			this.createObjectAndGenerateDiagram(currentDiagram.clone(), joinPointResult, actionAnnotation);
		}
		return joinPointResult;
	}

	@Around("(execution(* *(..)) || execution(*.new(..))) && @annotation(actionsAnnotation)")
	public Object actionsAnnotationAdvice(ProceedingJoinPoint joinPoint, Actions actionsAnnotation) throws Throwable {
		//TODO not working properly for multiple annotations
		//		ActivityDiagramModel currentDiagram = diagramsMap.get("1").clone();
		//		for (Action action : actionsAnnotation.value()) {
		//			if (joinPoint.getKind().contains("constructor")) { // joinpoint is a constructor call
		//				this.createObjectAndGenerateDiagramFromJoinPoint(new ActivityDiagramModel(currentDiagram), action, joinPoint);
		//			}
		//			if (joinPointResult != null) { // joinpoint is a method call
		//				this.createObjectAndGenerateDiagram(currentDiagram.clone(), joinPointResult, action);
		//			}
		//		}
		return joinPoint.proceed();
	}

	private void createObjectAndGenerateDiagram(ActivityDiagramModel currentDiagram, Object joinPointResult, Action actionAnnotation) throws Throwable {
		ObjectActivityDiagramElement objectElement = createObjectDiagramElementFromResult(joinPointResult);
		generateDiagramWithInsertedObject(currentDiagram, actionAnnotation, objectElement);
	}

	private void createObjectAndGenerateDiagramFromJoinPoint(ActivityDiagramModel currentDiagram, Action actionAnnotation, JoinPoint joinPoint) {
		ActivityDiagramElement objectElement = this.createObjectDiagramElement(joinPoint);
		this.generateDiagramWithInsertedObject(currentDiagram, actionAnnotation, objectElement);
	}

	private void generateDiagramWithInsertedObject(ActivityDiagramModel currentDiagram, Action actionAnnotation, ActivityDiagramElement objectElement) {
		Optional<ActivityDiagramElement> element;
		Optional<ActivityDiagramElement> successorOptional;
		element = currentDiagram.getDiagramElements().stream().filter(x -> x.getMessage().equals(resolvePropertyValue(actionAnnotation.message()))).findFirst();
		if (element.isPresent()) {
			ActivityDiagramElement currentElement = element.get();
			successorOptional = currentDiagram.getDiagramElements().stream()
											  .filter(x -> x.getParentMessage().equals(currentElement.getMessage()))
											  .findFirst();
			objectElement.setParentMessage(currentElement.getMessage());
			objectElement.setDiagramIdentifiers(currentElement.getDiagramIdentifiers());
			if (successorOptional.isPresent()) {
				ActivityDiagramElement successorElement = successorOptional.get();
				successorElement.setParentMessage(objectElement.getMessage());
				//successor is present, that means object element is going to be placed within the diagram
			} else {
				currentElement.setActionType(ActionType.ACTION);
				objectElement.setActionType(ActionType.END);
				//there is no successor, which means that currentElement should be ActionType.END -> ActionType needs to be altered, along with
			}
			currentDiagram.addElement(objectElement);
			VisualDiagramGenerator.generateImageFromPlantUmlString(currentDiagram.toPlantUmlString(),
																   currentDiagram.getDiagramIdentifier() + "_run_" + executionCount);
		}
		executionCount++;
	}

	private ObjectActivityDiagramElement createObjectDiagramElementFromResult(Object joinPointResult) throws Throwable {
		ObjectActivityDiagramElement objectElement = new ObjectActivityDiagramElement();
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

	private ActivityDiagramElement createObjectDiagramElement(JoinPoint joinPoint) {
		ObjectActivityDiagramElement objectElement = new ObjectActivityDiagramElement();
		ConstructorSignature signature = (ConstructorSignature) joinPoint.getSignature();
		StringBuilder objectMessageBuilder = new StringBuilder("<b>" + joinPoint.getTarget().getClass().getSimpleName() + "</b>\n");
		Object[] arguments = joinPoint.getArgs();
		String[] parameterNames = signature.getParameterNames();
		Class<?>[] parameterTypes = signature.getParameterTypes();

		for (int i = 0; i < signature.getParameterNames().length; ++i) {
			objectMessageBuilder.append(parameterNames[i])
								.append(": ")
								.append(parameterTypes[i].getSimpleName()).append(" = ")
								.append(arguments[i].toString())
								.append("\n");
		}
		objectMessageBuilder.insert(objectMessageBuilder.length() - 1, "]");
		objectElement.setMessage(objectMessageBuilder.toString().trim());
		return objectElement;
	}

	private String resolvePropertyValue(String key) {
		key = key != null ? key.replace("\"","").trim() : null;
		return properties != null && key != null ? properties.getProperty(key) : key;
	}
}
