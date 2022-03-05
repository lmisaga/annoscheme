package org.annoscheme.application.restockOrder;

import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.ActionType;

public class RestockOrderController {

	private final RestockOrderService restockOrderService = new RestockOrderService();

	@Action(actionType = ActionType.START, message="d1.start", diagramIdentifiers = {"d1.id"})
	public RestockOrderRequestModel parseRequest(String[] inputArguments) {
		Integer deviceId = Integer.parseInt(inputArguments[0]);
		Integer restockQuantity = Integer.parseInt(inputArguments[1]);
		return new RestockOrderRequestModel(deviceId, restockQuantity);
	}

	public RestockOrder createRestockOrder(String[] inputArguments) {
		return restockOrderService.createRestockOrder(parseRequest(inputArguments));
	}

	@Action(actionType = ActionType.START, message="d2.start", diagramIdentifiers = {"d2.id"})
	public void cancelRestockOrder(Integer restockOrderId) {
		restockOrderService.cancelRestockOrder(restockOrderId);
	}

}
