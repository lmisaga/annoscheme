package org.annoscheme.application.restockOrder;

import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.ActionType;

public class RestockOrderController {

	private final RestockOrderService restockOrderService = new RestockOrderService();

	@Action(actionType = ActionType.START, message="Parse request", diagramIdentifiers = {"1"})
	public RestockOrderRequestModel parseRequest(String[] inputArguments) {
		if (inputArguments == null || inputArguments.length < 2) {
			throw new IllegalArgumentException("Two arguments - device ID and restock quantity must be provided!");
		}
		Integer deviceId = Integer.parseInt(inputArguments[0]);
		Integer restockQuantity = Integer.parseInt(inputArguments[1]);
		return new RestockOrderRequestModel(deviceId, restockQuantity);
	}

	public RestockOrder createRestockOrder(String[] inputArguments) {
		return restockOrderService.createRestockOrder(parseRequest(inputArguments));
	}

	@Action(actionType = ActionType.START, message="Cancel restock order request received", diagramIdentifiers = {"2"})
	public void cancelRestockOrder(Integer restockOrderId) {
		restockOrderService.cancelRestockOrder(restockOrderId);
	}

}
