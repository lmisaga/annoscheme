package org.annoscheme.application.restockOrder;

import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.ActionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestockOrderRepository {

	private List<RestockOrder> restockOrdersMockStorage;

	public RestockOrderRepository() {
		this.restockOrdersMockStorage = new ArrayList<>();
		restockOrdersMockStorage.addAll(Arrays.asList(
				new RestockOrder(8, 1),
				new RestockOrder(10, 5)
		));
	}

	@Action(message = "d2.findResOrById", actionType = ActionType.ACTION, diagramIdentifiers = {"d2.id"}, parentMessage = "d2.start")
	public RestockOrder findRestockOrderById(Integer restockOrderId) {
		return this.restockOrdersMockStorage.stream()
											.filter(r -> r.getId().equals(restockOrderId))
											.findFirst()
											.orElse(null);
	}

	@Action(actionType = ActionType.ACTION, message = "d1.saveResOr", diagramIdentifiers = {"d1.id"}, parentMessage = "d1.createResOr")
	@Action(actionType = ActionType.END, message = "d2.saveResOr", diagramIdentifiers = {"d2.id"}, parentMessage = "d2.cancel")
	public RestockOrder insertRestockOrder(RestockOrder restockOrder) {
		this.restockOrdersMockStorage.add(restockOrder);
		return restockOrder;
	}

}

