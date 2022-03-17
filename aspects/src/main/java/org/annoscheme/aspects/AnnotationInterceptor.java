package org.annoscheme.aspects;

import org.annoscheme.aspects.process.JoinPointProcessor;
import org.annoscheme.aspects.process.model.RequestData;
import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.ActionType;
import org.annoscheme.common.annotation.Actions;
import org.annoscheme.common.io.DiagramSerializer;
import org.annoscheme.common.io.VisualDiagramGenerator;
import org.annoscheme.common.model.ActivityDiagramModel;
import org.annoscheme.common.model.element.ActivityDiagramElement;
import org.annoscheme.common.model.element.ObjectActivityDiagramElement;
import org.annoscheme.common.properties.PropertiesHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.annoscheme.common.model.constants.PlantUmlConstants.B;
import static org.annoscheme.common.model.constants.PlantUmlConstants.B_END;

@Aspect
public class AnnotationInterceptor {

	private final HashMap<String, ActivityDiagramModel> diagramsMap = DiagramSerializer.deserializeCachedDiagramsMap();

	private final PropertiesHandler propertiesHandler = PropertiesHandler.getInstance();

	private final Logger logger = LoggerFactory.getLogger(AnnotationInterceptor.class);

	private final JoinPointProcessor joinPointProcessor = new JoinPointProcessor();

	private Integer executionCount = 1;

	private String currentlyActiveDiagram = null;

	@Around("(execution(* *(..)) || execution(*.new(..))) && @annotation(actionAnnotation)")
	public Object actionAnnotationAdvice(ProceedingJoinPoint joinPoint, Action actionAnnotation) throws Throwable {
		String resolvedIdentifier = propertiesHandler.resolvePropertyValue(actionAnnotation.diagramIdentifiers()[0]);
		ActivityDiagramModel currentDiagram = diagramsMap.get(resolvedIdentifier);
		if (ActionType.START.equals(actionAnnotation.actionType())) {
			currentDiagram.removeObjectElements();
			this.currentlyActiveDiagram = resolvedIdentifier;
		} else if (!resolvedIdentifier.equals(this.currentlyActiveDiagram)) {
			return joinPoint.proceed();
		}
		if (joinPoint.getKind().contains("constructor")) { // joinpoint is a constructor call
			this.createObjectAndGenerateDiagramFromJoinPoint(new ActivityDiagramModel(currentDiagram), actionAnnotation, joinPoint);
			return joinPoint.proceed();
		} else {
			//joinPoint is a method call, verify if is a REST controller action
			if (joinPointProcessor.isRestControllerMethod(joinPoint)) {
				RequestData requestData = joinPointProcessor.getRequestData(joinPoint);
				if (requestData != null) {
					this.createObjectAndGenerateDiagramFromRequestData(currentDiagram, requestData, actionAnnotation);
					return joinPoint.proceed();
				}
			}
			//not a REST controller method call, proceed
			Object joinPointResult = joinPoint.proceed();
			this.createObjectAndGenerateDiagram(currentDiagram, joinPointResult, actionAnnotation);
			return joinPointResult;

		}
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

	private void createObjectAndGenerateDiagramFromRequestData(ActivityDiagramModel currentDiagram, RequestData requestData, Action actionAnnotation)
			throws Throwable {
		ObjectActivityDiagramElement objectElement = createObjectDiagramElementFromRequestData(requestData);
		generateDiagramWithInsertedObject(currentDiagram, actionAnnotation, objectElement);
	}

	private void createObjectAndGenerateDiagramFromJoinPoint(ActivityDiagramModel currentDiagram, Action actionAnnotation, JoinPoint joinPoint) {
		ActivityDiagramElement objectElement = this.createObjectDiagramElement(joinPoint);
		this.generateDiagramWithInsertedObject(currentDiagram, actionAnnotation, objectElement);
	}

	//TODO refactor, move to separate classes
	private void generateDiagramWithInsertedObject(ActivityDiagramModel currentDiagram, Action actionAnnotation, ActivityDiagramElement objectElement) {
		Optional<ActivityDiagramElement> element;
		Optional<ActivityDiagramElement> successorOptional;
		element = currentDiagram.getDiagramElements()
								.stream()
								.filter(x -> x.getMessage().equals(this.propertiesHandler.resolvePropertyValue(actionAnnotation.message())))
								.findFirst();
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

	private ObjectActivityDiagramElement createObjectDiagramElementFromRequestData(RequestData requestData) throws IllegalAccessException {
		ObjectActivityDiagramElement objectElement = new ObjectActivityDiagramElement();
		StringBuilder objectMessageBuilder = new StringBuilder();
		// write pathVars
		if (!requestData.getPathVariablesMap().isEmpty()) {
			objectMessageBuilder.append(B+ "Path variables" + B_END + "\n");
			requestData.getPathVariablesMap().forEach((key, value) -> objectMessageBuilder.append(key).append(" = ").append(value.toString()).append("\n"));
		}
		//write params
		if (!requestData.getParamsMap().isEmpty()) {
			objectMessageBuilder.append(B + "Params" + B_END + "\n");
			for (Map.Entry<String, Object> entry : requestData.getParamsMap().entrySet()) {
				objectMessageBuilder.append(entry.getKey()).append(" = ").append(entry.getValue().toString()).append("\n");
				System.out.println(objectMessageBuilder);
			}
		}
		//write body
		if (requestData.getRequestBody() != null) {
			Object body = requestData.getRequestBody();
			objectMessageBuilder.append(B + "Body: ").append(body.getClass().getSimpleName()).append(B_END).append("\n");
			Field[] fields = body.getClass().getDeclaredFields();
			AccessibleObject.setAccessible(fields, true);
			for (Field field : fields) {
				if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
					objectMessageBuilder.append(field.getName())
										.append(": ")
										.append(field.getType().getSimpleName()).append(" = ").append(field.get(body))
										.append("\n");
				}
			}
		}
		objectMessageBuilder.insert(objectMessageBuilder.length() - 1, "]");
		objectElement.setMessage(objectMessageBuilder.toString().trim());
		return objectElement;
	}

	private ObjectActivityDiagramElement createObjectDiagramElementFromResult(Object joinPointResult) throws Throwable {
		ObjectActivityDiagramElement objectElement = new ObjectActivityDiagramElement();
		StringBuilder objectMessageBuilder = new StringBuilder(B + joinPointResult.getClass().getSimpleName() + B_END + "\n");
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
		StringBuilder objectMessageBuilder = new StringBuilder(B + joinPoint.getTarget().getClass().getSimpleName() + B_END + "\n");
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
}
