package org.annoscheme.aspects;

import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.io.ObjectSerializer;
import org.annoscheme.common.io.VisualDiagramGenerator;
import org.annoscheme.common.model.ActivityDiagramModel;
import org.annoscheme.common.model.DiagramElement;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

@Aspect
public class AnnotationInterceptor {

	private static final Logger logger = Logger.getLogger(AnnotationInterceptor.class);

	private List<ActivityDiagramModel> diagramModels = ObjectSerializer.deserializeCachedDiagramList();

	@Around("@annotation(org.annoscheme.common.annotation.Action)")
	public Object annotation(ProceedingJoinPoint joinPoint) throws Throwable {
//		logger.info(joinPoint);
//		logger.info(diagramModels);
//		return joinPoint.proceed();
		Object res = null;
		res = joinPoint.proceed();
		if (res == null)
			return res;
		Class<?> c1 = res.getClass();
		Field[] fields = c1.getDeclaredFields();
		MethodSignature methodSignature = null;
		ConstructorSignature constructorSignature = null;
		try {
			methodSignature = (MethodSignature) joinPoint.getSignature();
		} catch (ClassCastException ex) {
			constructorSignature = (ConstructorSignature) joinPoint.getSignature();
		}
		Action actionAnnotation = methodSignature != null ? methodSignature.getMethod().getAnnotation(Action.class) :
								  constructorSignature != null ? (Action) constructorSignature.getConstructor().getAnnotation(Action.class)
															   : null;
		logger.info(actionAnnotation);
		DiagramElement element = diagramModels.get(0).getDiagramElements().stream().filter(x -> x.getMessage().equals(actionAnnotation.message())).findFirst().get();
		DiagramElement successorElement = diagramModels.get(0).getDiagramElements().get(diagramModels.get(0).getDiagramElements().indexOf(element)+1);
		diagramModels.get(0).getDiagramElements().add(createObjectDiagramElement(res, actionAnnotation, successorElement, methodSignature));
		VisualDiagramGenerator.generateImageFromPlantUmlString(diagramModels.get(0).toPlantUmlString(), "test");
		AccessibleObject.setAccessible(fields, true);
		for (Field field : fields) {
			if (!Modifier.isStatic(field.getModifiers())) {
				System.out.println(field + " = " + field.get(res));
			}
		}
		return res;
	}

	private DiagramElement createObjectDiagramElement(Object joinPointResult, Action extractedAnnotation, DiagramElement successor,
													  MethodSignature methodSignature) throws Throwable {
		DiagramElement objectElement = new DiagramElement();
		StringBuilder objectMessageBuilder = new StringBuilder("<b>" + joinPointResult.getClass().getSimpleName() + "</b>");
		Field[] fields = joinPointResult.getClass().getDeclaredFields();
		AccessibleObject.setAccessible(fields, true);
		for (Field field : fields) {
			if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
				objectMessageBuilder.append(field).append(": ").append(field.getType().getSimpleName()).append(" = ").append(field.get(joinPointResult));
			}
		}
		objectElement.setParentMessage(extractedAnnotation.message());
		objectElement.setDiagramIdentifiers(successor.getDiagramIdentifiers());
		successor.setParentMessage(objectElement.getMessage());
		return objectElement;
	}
}
