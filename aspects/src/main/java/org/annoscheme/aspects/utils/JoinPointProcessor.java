package org.annoscheme.aspects.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class JoinPointProcessor {

	private static final Logger logger = LoggerFactory.getLogger(JoinPointProcessor.class);

	public static boolean isRestControllerMethod(ProceedingJoinPoint joinPoint) {
		List<Class<? extends Annotation>> annotations = Arrays.asList(RequestMapping.class,
																	  GetMapping.class,
																	  PostMapping.class,
																	  PutMapping.class);
		Method method = getMethodFromJoinPointSignature(joinPoint);

		Optional<Class<? extends Annotation>> foundAnnotation = annotations
				.stream()
				.filter(annotation -> AnnotationUtils.findAnnotation(method, annotation) != null)
				.findFirst();
		return foundAnnotation.isPresent();

	}

	public static Object getRequestBodyArgumentFromJoinPoint(ProceedingJoinPoint joinPoint) {
		Method method = getMethodFromJoinPointSignature(joinPoint);
		Object foundParameter = null;
		if (method != null) {
			Parameter[] parameters = method.getParameters();
			for (int i = 0; i < parameters.length; i++) {
				if (AnnotationUtils.findAnnotation(parameters[i], RequestBody.class) != null) {
					foundParameter = joinPoint.getArgs()[i];
					break;
				}
			}
		}
		return foundParameter;
	}

	private static Method getMethodFromJoinPointSignature(ProceedingJoinPoint joinPoint) {
		try {
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			return signature.getMethod();
		} catch (ClassCastException classCastEx) {
			logger.debug(classCastEx.toString());
		} catch (Exception exception) {
			logger.debug(exception.getMessage());
		}
		return null;
	}

}
