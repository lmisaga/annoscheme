package com.sclad.scladapp.mapper;

import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.ActionType;
import org.springframework.stereotype.Component;

import com.sclad.scladapp.entity.RestockOrder;
import com.sclad.scladapp.model.RestockOrderModel;

@Component
public class RestockOrderMapper extends AbstractEntityMapper<RestockOrder, RestockOrderModel> {

	protected RestockOrderMapper() {
		super(RestockOrder.class, RestockOrderModel.class);
	}

	@Override
	@Action(actionType = ActionType.END, message = "resOr.create.mapToDto", parentMessage = "resOr.create.logSuccess", diagramIdentifiers = {"resOr.create"})
	public RestockOrderModel toDto(RestockOrder restockOrder) {
		if (restockOrder.getDevice() == null) {
			throw new IllegalStateException("Invalid restock order entity state");
		}
		return super.toDto(restockOrder);
	}
}
