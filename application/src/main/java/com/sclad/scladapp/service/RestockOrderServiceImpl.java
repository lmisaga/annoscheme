package com.sclad.scladapp.service;

import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.BranchingType;
import org.annoscheme.common.annotation.Conditional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sclad.scladapp.entity.Device;
import com.sclad.scladapp.entity.RestockOrder;
import com.sclad.scladapp.exceptions.DeviceNotFoundException;
import com.sclad.scladapp.mapper.RestockOrderMapper;
import com.sclad.scladapp.model.RestockOrderCreateModel;
import com.sclad.scladapp.model.RestockOrderModel;
import com.sclad.scladapp.repository.DeviceRepository;
import com.sclad.scladapp.repository.RestockOrderRepository;

@Service
public class RestockOrderServiceImpl implements RestockOrderService {

	private static final Logger logger = LoggerFactory.getLogger(RestockOrderServiceImpl.class);

	private final RestockOrderRepository restockOrderRepository;
	private final DeviceRepository deviceRepository;
	private final DeviceService deviceService;
	private final RestockOrderMapper restockOrderMapper;

	public RestockOrderServiceImpl(RestockOrderRepository restockOrderRepository, DeviceRepository deviceRepository, DeviceService deviceService,
								   RestockOrderMapper restockOrderMapper) {
		this.restockOrderRepository = restockOrderRepository;
		this.deviceRepository = deviceRepository;
		this.deviceService = deviceService;
		this.restockOrderMapper = restockOrderMapper;
	}

	@Override
	@Action(message = "resOr.create.createResOr", parentMessage = "resOr.create.findDeviceById", diagramIdentifiers = {"resOr.create"})
	@Conditional(condition = "resOr.create.foundDevice?", type = BranchingType.MAIN, diagramIdentifiers = {"resOr.create"})
	public RestockOrderModel create(RestockOrderCreateModel model) {
		RestockOrder restockOrder = new RestockOrder();
		Device device = deviceService.getById(model.getDeviceId());
		if (deviceService.getById(model.getDeviceId()) != null) {
			restockOrder.setDevice(device);
			device.setReordered(Boolean.TRUE);
			deviceRepository.save(device);
		}
		restockOrder.setProductName(device.getProductName());
		restockOrder.setDeviceType(device.getDeviceType());
		restockOrder.setQuantityToReorder(model.getQuantityToReorder());
		restockOrder.setSendNotification(model.getSendNotification());
		restockOrderRepository.save(restockOrder);
		RestockOrderModel createdModel = restockOrderMapper.toDto(restockOrder);
		this.logSuccess(restockOrder);
		return createdModel;
	}

	@Override
	public RestockOrder getById(Long id) {
		return restockOrderRepository.findById(id)
									 .orElseThrow(() -> new DeviceNotFoundException(id));
	}

	@Action(message = "resOr.create.logSuccess", diagramIdentifiers = {"resOr.create"}, parentMessage = "resOr.create.createResOr")
	private void logSuccess(RestockOrder restockOrder) {
		logger.info("Created restock order ID {} for device {} successfully.", restockOrder.getId(), restockOrder.getDevice().getId());
	}
}
