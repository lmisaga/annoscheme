package com.sclad.scladapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import com.sclad.scladapp.entity.RestockOrder;
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

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public RestockOrder create(@RequestBody @Valid RestockOrderModel model) {
        return restockOrderService.create(model);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public RestockOrder getById(@PathVariable Long id) {
        return restockOrderService.getById(id);
    }
}
