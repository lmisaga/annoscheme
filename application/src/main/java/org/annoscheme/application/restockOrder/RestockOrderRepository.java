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

	public RestockOrder findRestockOrderById(Integer restockOrderId) {
		return this.restockOrdersMockStorage.stream()
											.filter(r -> r.getId().equals(restockOrderId))
											.findFirst()
											.orElse(null);
	}

	@Action(actionType = ActionType.END,
			message="Save restock order",
			diagramIdentifiers = {"1"},
			parentMessage = "Create restock order")
	public void insertRestockOrder(RestockOrder restockOrder) {
		this.restockOrdersMockStorage.add(restockOrder);
	}

}
