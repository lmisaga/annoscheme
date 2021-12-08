package org.annoscheme.application.restockOrder;

import org.annoscheme.application.device.Device;
import org.annoscheme.application.device.DeviceRepository;
import org.annoscheme.application.device.exception.DeviceNotFoundException;
import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.ActionType;
import org.annoscheme.common.annotation.BranchingType;
import org.annoscheme.common.annotation.Conditional;

public class RestockOrderService {

	private final DeviceRepository deviceRepository;
	private final RestockOrderRepository restockOrderRepository;

	public RestockOrderService() {
		this.deviceRepository = new DeviceRepository();
		this.restockOrderRepository = new RestockOrderRepository();
	}

	@Action(diagramIdentifiers = {"d1.id"}, message = "d1.createResOr", parentMessage = "d1.findById")
	@Conditional(type = BranchingType.MAIN, condition = "d1.deviceCond", diagramIdentifiers = {"d1.id"})
	public RestockOrder createRestockOrder(RestockOrderRequestModel requestModel) {
		Device device = this.deviceRepository.findDeviceById(requestModel.getDeviceId());
		if (device != null) {
			return restockOrderRepository.insertRestockOrder(new RestockOrder(requestModel.getDeviceId(), requestModel.getRestockQuantity()));
		} else {
			throw new DeviceNotFoundException("Device was not found.");
		}
	}

	@Action(actionType = ActionType.ACTION, message = "d2.cancel", diagramIdentifiers = {"d2.id"}, parentMessage = "d2.findResOrById")
	public void cancelRestockOrder(Integer restockOrderId) {
		RestockOrder orderToCancel = this.restockOrderRepository.findRestockOrderById(restockOrderId);
		orderToCancel.setQuantityToRestock(0); // just for demonstration
		this.restockOrderRepository.insertRestockOrder(orderToCancel);
	}
}
