package org.annoscheme.aspects;

import org.annoscheme.common.io.ObjectSerializer;
import org.annoscheme.common.model.ActivityDiagramModel;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.List;

@Aspect
public class AnnotationInterceptor {

	private static final Logger logger = Logger.getLogger(AnnotationInterceptor.class);

	private List<ActivityDiagramModel> diagramModels = ObjectSerializer.deserializeCachedDiagramList();

	@Around("@annotation(org.annoscheme.common.annotation.Action)")
	public Object annotation(ProceedingJoinPoint joinPoint) throws Throwable {
		logger.info(joinPoint);
		logger.info(diagramModels);
		return joinPoint.proceed();
	}
}
