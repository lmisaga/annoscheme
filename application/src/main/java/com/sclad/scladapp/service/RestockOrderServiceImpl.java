package com.sclad.scladapp.service;

import org.springframework.stereotype.Service;

import com.sclad.scladapp.entity.RestockOrder;
import com.sclad.scladapp.exceptions.DeviceNotFoundException;
import com.sclad.scladapp.model.RestockOrderModel;
import com.sclad.scladapp.repository.DeviceRepository;
import com.sclad.scladapp.repository.RestockOrderRepository;

@Service
public class RestockOrderServiceImpl implements RestockOrderService {

    private final RestockOrderRepository restockOrderRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceService deviceService;

    public RestockOrderServiceImpl(RestockOrderRepository restockOrderRepository, DeviceRepository deviceRepository, DeviceService deviceService) {
        this.restockOrderRepository = restockOrderRepository;
        this.deviceRepository = deviceRepository;
        this.deviceService = deviceService;
    }

    @Override
    public RestockOrder create(RestockOrderModel model) {
        RestockOrder restockOrder = new RestockOrder();
        if (deviceService.getById(model.getDevice().getId()) != null) {
            restockOrder.setDevice(model.getDevice());
            model.getDevice().setReordered(Boolean.TRUE);
            deviceRepository.save(model.getDevice());
        }
        restockOrder.setProductName(model.getProductName());
        restockOrder.setDeviceType(model.getDeviceType());
        restockOrder.setQuantityToReorder(model.getQuantityToReorder());
        restockOrder.setSendNotification(model.getSendNotification());
        restockOrderRepository.save(restockOrder);
        return restockOrder;
    }

    @Override
    public RestockOrder getById(Long id) {
        return restockOrderRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));
    }
}
