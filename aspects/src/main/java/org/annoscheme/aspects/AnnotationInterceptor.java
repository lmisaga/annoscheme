package org.annoscheme.aspects;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AnnotationInterceptor {

	private static final Logger logger = Logger.getLogger(AnnotationInterceptor.class);

	@Around("@annotation(org.annoscheme.common.annotation.Action)")
	public Object annotation(ProceedingJoinPoint joinPoint) throws Throwable {
		logger.info(joinPoint);
		return joinPoint.proceed();
	}
}
