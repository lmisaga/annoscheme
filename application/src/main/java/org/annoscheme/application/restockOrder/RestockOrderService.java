package org.annoscheme.application.restockOrder;

import org.annoscheme.application.device.Device;
import org.annoscheme.application.device.DeviceRepository;
import org.annoscheme.application.device.exception.DeviceNotFoundException;
import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.BranchingType;
import org.annoscheme.common.annotation.Conditional;

public class RestockOrderService {

	private final DeviceRepository deviceRepository;
	private final RestockOrderRepository restockOrderRepository;

	public RestockOrderService() {
		this.deviceRepository = new DeviceRepository();
		this.restockOrderRepository = new RestockOrderRepository();
	}

	@Action(diagramIdentifiers = {"1"}, message = "Create restock order", parentMessage = "Find device by ID")
	@Conditional(type = BranchingType.MAIN, condition = "Found device?")
	public RestockOrder createRestockOrder(RestockOrderRequestModel requestModel) {
		System.out.println("createRestockOrder");
		Device device = this.deviceRepository.findDeviceById(requestModel.getDeviceId());
		if (device != null) {
			return restockOrderRepository.insertRestockOrder(new RestockOrder(requestModel.getDeviceId(), requestModel.getRestockQuantity()));
		} else {
			throw new DeviceNotFoundException("Device was not found.");
		}
	}
}
