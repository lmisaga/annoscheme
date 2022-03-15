package com.sclad.scladapp.service;

import com.sclad.scladapp.entity.RestockOrder;
import com.sclad.scladapp.model.RestockOrderCreateModel;
import com.sclad.scladapp.model.RestockOrderModel;

public interface RestockOrderService extends AbstractService<RestockOrder> {

	RestockOrderModel create(RestockOrderCreateModel model);
}
