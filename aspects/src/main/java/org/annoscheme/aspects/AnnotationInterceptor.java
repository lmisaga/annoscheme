package org.annoscheme.aspects;

import org.annoscheme.aspects.process.JoinPointProcessor;
import org.annoscheme.aspects.process.ObjectElementProcessor;
import org.annoscheme.aspects.process.model.RequestData;
import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.ActionType;
import org.annoscheme.common.annotation.Actions;
import org.annoscheme.common.io.DiagramSerializer;
import org.annoscheme.common.model.ActivityDiagramModel;
import org.annoscheme.common.model.element.ActivityDiagramElement;
import org.annoscheme.common.model.element.ObjectActivityDiagramElement;
import org.annoscheme.common.properties.PropertiesHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

@Aspect
public class AnnotationInterceptor {

	private final HashMap<String, ActivityDiagramModel> diagramsMap = DiagramSerializer.deserializeCachedDiagramsMap();

	private final PropertiesHandler propertiesHandler = PropertiesHandler.getInstance();

	private final Logger logger = LoggerFactory.getLogger(AnnotationInterceptor.class);

	private final JoinPointProcessor joinPointProcessor = new JoinPointProcessor();
	private final ObjectElementProcessor objectElementProcessor = new ObjectElementProcessor();

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
		if (joinPointResult != null) {
			ObjectActivityDiagramElement objectElement = objectElementProcessor.createObjectDiagramElementFromResult(joinPointResult);
			objectElementProcessor.generateDiagramWithInsertedObject(currentDiagram, actionAnnotation, objectElement, this.executionCount++);
		}
	}

	private void createObjectAndGenerateDiagramFromRequestData(ActivityDiagramModel currentDiagram, RequestData requestData, Action actionAnnotation)
			throws Throwable {
		ObjectActivityDiagramElement objectElement = objectElementProcessor.createObjectDiagramElementFromRequestData(requestData);
		objectElementProcessor.generateDiagramWithInsertedObject(currentDiagram, actionAnnotation, objectElement, this.executionCount++);
	}

	private void createObjectAndGenerateDiagramFromJoinPoint(ActivityDiagramModel currentDiagram, Action actionAnnotation, JoinPoint joinPoint) {
		ActivityDiagramElement objectElement = objectElementProcessor.createObjectDiagramElement(joinPoint);
		objectElementProcessor.generateDiagramWithInsertedObject(currentDiagram, actionAnnotation, objectElement, this.executionCount++);
	}

}
