package org.annoscheme.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AnnotationInterceptor {

	@Around("@annotation(org.annoscheme.common.annotation.Action)")
	public Object annotation(ProceedingJoinPoint joinPoint) throws Throwable {
		System.out.println(joinPoint.getSignature());
		return joinPoint.proceed();
	}
}
