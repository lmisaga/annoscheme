package org.annoscheme.aspects.process;

import org.annoscheme.aspects.process.model.RequestData;
import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.ActionType;
import org.annoscheme.common.io.VisualDiagramGenerator;
import org.annoscheme.common.model.ActivityDiagramModel;
import org.annoscheme.common.model.element.ActivityDiagramElement;
import org.annoscheme.common.model.element.ObjectActivityDiagramElement;
import org.annoscheme.common.properties.PropertiesHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Optional;

import static org.annoscheme.common.model.constants.PlantUmlConstants.B;
import static org.annoscheme.common.model.constants.PlantUmlConstants.B_END;

public class ObjectElementProcessor {

	private final PropertiesHandler propertiesHandler = PropertiesHandler.getInstance();

	private final Logger logger = LoggerFactory.getLogger(ObjectElementProcessor.class);

	public ActivityDiagramElement createObjectDiagramElement(JoinPoint joinPoint) {
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

	public ObjectActivityDiagramElement createObjectDiagramElementFromResult(Object joinPointResult) {
		ObjectActivityDiagramElement objectElement = new ObjectActivityDiagramElement();
		StringBuilder objectMessageBuilder = new StringBuilder(B + joinPointResult.getClass().getSimpleName() + B_END + "\n");
		this.parseObjectFields(joinPointResult, objectMessageBuilder);
		objectMessageBuilder.insert(objectMessageBuilder.length() - 1, "]");
		objectElement.setMessage(objectMessageBuilder.toString().trim());
		return objectElement;
	}

	public ObjectActivityDiagramElement createObjectDiagramElementFromRequestData(RequestData requestData) {
		ObjectActivityDiagramElement objectElement = new ObjectActivityDiagramElement();
		StringBuilder objectMessageBuilder = new StringBuilder();
		// write pathVars
		if (!requestData.getPathVariablesMap().isEmpty()) {
			objectMessageBuilder.append(B + "Path variables" + B_END + "\n");
			requestData.getPathVariablesMap().forEach((key, value) -> objectMessageBuilder.append(key).append(" = ").append(value.toString()).append("\n"));
		}
		//write params
		if (!requestData.getParamsMap().isEmpty()) {
			objectMessageBuilder.append(B + "Params" + B_END + "\n");
			for (Map.Entry<String, Object> entry : requestData.getParamsMap().entrySet()) {
				objectMessageBuilder.append(entry.getKey()).append(" = ").append(entry.getValue().toString()).append("\n");
			}
		}
		//write body
		if (requestData.getRequestBody() != null) {
			Object body = requestData.getRequestBody();
			objectMessageBuilder.append(B + "Body: ").append(body.getClass().getSimpleName()).append(B_END).append("\n");
			this.parseObjectFields(body, objectMessageBuilder);
		}
		objectMessageBuilder.insert(objectMessageBuilder.length() - 1, "]");
		objectElement.setMessage(objectMessageBuilder.toString().trim());
		return objectElement;
	}

	public void generateDiagramWithInsertedObject(ActivityDiagramModel currentDiagram, Action actionAnnotation, ActivityDiagramElement objectElement,
												  int executionCount) {
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
	}

	private void parseObjectFields(Object object, StringBuilder messageBuilder) {
		try {
			Field[] fields = object.getClass().getDeclaredFields();
			AccessibleObject.setAccessible(fields, true);
			for (Field field : fields) {
				if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
					messageBuilder.append(field.getName())
								  .append(": ")
								  .append(field.getType().getSimpleName()).append(" = ").append(field.get(object))
								  .append("\n");
				}
			}
		} catch (IllegalAccessException err) {
			logger.error("Unable to parse fields from object: " + err);
		}
	}

}
