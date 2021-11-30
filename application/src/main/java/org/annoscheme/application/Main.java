package org.annoscheme.application;

import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.ActionType;
import org.annoscheme.application.restockOrder.RestockOrderRequestModel;
import org.annoscheme.application.restockOrder.RestockOrderService;

public class Main {

	//1st arg -> deviceId, 2nd arg -> restock
	public static void main(String[] args) {
		RestockOrderRequestModel requestModel = parseArguments(args);
		RestockOrderService restockOrderService = new RestockOrderService();
		restockOrderService.createRestockOrder(requestModel);

	}

	@Action(actionType = ActionType.START,
			message="Parse request",
			diagramIdentifiers = {"1"})
	private static RestockOrderRequestModel parseArguments(String[] inputArguments) {
		if (inputArguments == null || inputArguments.length < 2) {
			throw new IllegalArgumentException("Two arguments - device ID and restock quantity must be provided!");
		}
		Integer deviceId = Integer.parseInt(inputArguments[0]);
		Integer restockQuantity = Integer.parseInt(inputArguments[1]);
		return new RestockOrderRequestModel(deviceId, restockQuantity);
	}
}
