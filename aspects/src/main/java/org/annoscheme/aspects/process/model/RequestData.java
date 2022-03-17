package org.annoscheme.aspects.process.model;

import java.util.HashMap;

/**
 * Serves as a metamodel for rendering object elements to AD.
 * Members kept in original java.lang.Object form to delay reflection need to the
 * point of processing to actual object elements.
 */
public final class RequestData {

	private final Object requestBody;

	private final HashMap<String, Object> paramsMap;

	private final HashMap<String, Object> pathVariablesMap;

	public RequestData(Object requestBody, HashMap<String, Object> paramsMap, HashMap<String, Object> pathVariablesMap) {
		this.requestBody = requestBody;
		this.paramsMap = paramsMap;
		this.pathVariablesMap = pathVariablesMap;
	}

	public Object getRequestBody() {
		return requestBody;
	}

	public HashMap<String, Object> getParamsMap() {
		return paramsMap;
	}

	public HashMap<String, Object> getPathVariablesMap() {
		return pathVariablesMap;
	}

}
