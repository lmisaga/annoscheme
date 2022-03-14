package org.annoscheme.aspects.process;

import org.annoscheme.aspects.process.model.RequestData;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class JoinPointProcessor {

	private static final Logger logger = LoggerFactory.getLogger(JoinPointProcessor.class);

	private final List<Class<? extends Annotation>> restMethodAnnotations = Arrays.asList(RequestMapping.class,
																						  GetMapping.class,
																						  PostMapping.class,
																						  PutMapping.class,
																						  DeleteMapping.class
	);

	public boolean isRestControllerMethod(ProceedingJoinPoint joinPoint) {
		Method method = getMethodFromJoinPointSignature(joinPoint);
		if (method != null) {
			return restMethodAnnotations.stream().anyMatch(x -> method.getAnnotation(x) != null);
		}
		return false;

	}

	public RequestData getRequestData(ProceedingJoinPoint joinPoint) {
		Method method = getMethodFromJoinPointSignature(joinPoint);
		return method != null && joinPoint.getArgs().length > 0 ? this.getRequestData(joinPoint, method) : null;
	}

	private RequestData getRequestData(ProceedingJoinPoint joinPoint, Method method) {
		//found objects
		HashMap<String, Object> paramsMap = new HashMap<>();
		HashMap<String, Object> pathVarsMap = new HashMap<>();
		Object requestBody = null;
		//required for reflection operations
		Parameter[] methodParams = method.getParameters();
		Object[] joinPointArgs = joinPoint.getArgs();
		String[] argNames = parseArgNames(joinPoint);

		for (int i = 0; i < methodParams.length; i++) {
			if (methodParams[i].getAnnotations().length == 0) {
				continue;
			}
			Annotation[] paramAnnotations = methodParams[i].getAnnotations();
			for (Annotation paramAnnotation : paramAnnotations) {
				if (paramAnnotation.annotationType().equals(PathVariable.class)) {
					pathVarsMap.put(argNames[i], joinPointArgs[i]);
				}
				if (paramAnnotation.annotationType().equals(RequestParam.class)) {
					paramsMap.put(argNames[i], joinPointArgs[i]);
				}
				if (paramAnnotation.annotationType().equals(RequestBody.class)) {
					requestBody = joinPointArgs[i];
				}
			}
		}
		return new RequestData(requestBody, paramsMap, pathVarsMap);
	}

	private String[] parseArgNames(ProceedingJoinPoint joinPoint) {
		try {
			CodeSignature signature = (CodeSignature) joinPoint.getSignature();
			return signature != null ? signature.getParameterNames() : new String[]{};
		} catch (ClassCastException ex) {
			logger.error("Can not parse argument names: " + ex);
		}
		return new String[]{};
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
