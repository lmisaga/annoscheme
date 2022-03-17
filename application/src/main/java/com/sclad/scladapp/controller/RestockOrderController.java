package com.sclad.scladapp.controller;

import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import com.sclad.scladapp.entity.RestockOrder;
import com.sclad.scladapp.model.RestockOrderCreateModel;
import com.sclad.scladapp.model.RestockOrderModel;
import com.sclad.scladapp.service.RestockOrderService;

@RestController
@RequestMapping(value = "/api/restockOrder")
public class RestockOrderController {

	private final RestockOrderService restockOrderService;

	@Autowired
	public RestockOrderController(RestockOrderService restockOrderService) {
		this.restockOrderService = restockOrderService;
	}

	@Action(actionType = ActionType.START, message = "resOr.create.receiveRequest", diagramIdentifiers = {"resOr.create"})
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public RestockOrderModel create(@RequestBody @Valid RestockOrderCreateModel model, @RequestParam("testParam") Integer test,
									@RequestParam("testParamString") String testString) {
		return restockOrderService.create(model);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public RestockOrder getById(@PathVariable Long id) {
		return restockOrderService.getById(id);
	}
}
