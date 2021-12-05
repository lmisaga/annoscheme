package org.annoscheme.aspects;

import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.io.ObjectSerializer;
import org.annoscheme.common.model.ActivityDiagramModel;
import org.annoscheme.common.model.DiagramElement;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		Action actionAnnotation = methodSignature.getMethod().getAnnotation(Action.class);
		logger.info(actionAnnotation);
		DiagramElement element = diagramModels.get(0).getDiagramElements().stream().filter(x -> x.getMessage().equals(actionAnnotation.message())).findFirst().get();
		DiagramElement successorElement = diagramModels.get(0).getDiagramElements().get(diagramModels.get(0).getDiagramElements().indexOf(element)+1);
		DiagramElement objectElement = new DiagramElement();
		objectElement.setParentMessage(actionAnnotation.message());
		objectElement.setMessage(res.getClass().getSimpleName() + "]");
		objectElement.setDiagramIdentifiers(element.getDiagramIdentifiers());
		diagramModels.get(0).getDiagramElements().add(diagramModels.get(0).getDiagramElements().indexOf(element)+1, objectElement);
		successorElement.setParentMessage(objectElement.getMessage());

		AccessibleObject.setAccessible(fields, true);
		for (Field field : fields) {
			if (!Modifier.isStatic(field.getModifiers())) {
				System.out.println(field + " = " + field.get(res));
			}
		}
		return res;
	}
}
