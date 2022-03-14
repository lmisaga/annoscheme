package org.annoscheme.application.restockOrder;

import org.annoscheme.application.device.Device;
import org.annoscheme.application.device.DeviceRepository;
import org.annoscheme.application.device.exception.DeviceNotFoundException;
import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.ActionType;
import org.annoscheme.common.annotation.BranchingType;
import org.annoscheme.common.annotation.Conditional;
import org.annoscheme.common.annotation.Joining;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestockOrderService {

	private final DeviceRepository deviceRepository;
	private final RestockOrderRepository restockOrderRepository;

	private static final Logger logger = LoggerFactory.getLogger(RestockOrderService.class);

	public RestockOrderService() {
		this.deviceRepository = new DeviceRepository();
		this.restockOrderRepository = new RestockOrderRepository();
	}

	@Action(diagramIdentifiers = {"d1.id"}, message = "d1.createResOr", parentMessage = "d1.findById")
	@Conditional(type = BranchingType.MAIN, condition = "d1.deviceCond", diagramIdentifiers = {"d1.id"})
	public RestockOrder createRestockOrder(RestockOrderRequestModel requestModel) {
		try {
			Device device = this.deviceRepository.findDeviceById(requestModel.getDeviceId());
			if (device != null) {
				logCreateResponse(true, "OK");
				return restockOrderRepository.insertRestockOrder(new RestockOrder(requestModel.getDeviceId(), requestModel.getRestockQuantity()));
			} else {
				throw new DeviceNotFoundException("Device was not found.");
			}
		} catch (DeviceNotFoundException exception) {
			logCreateResponse(false, "NOT OK");
		}
		return null;
	}

	@Action(actionType = ActionType.ACTION, message = "d2.cancel", diagramIdentifiers = {"d2.id"}, parentMessage = "d2.findResOrById")
	public void cancelRestockOrder(Integer restockOrderId) {
		RestockOrder orderToCancel = this.restockOrderRepository.findRestockOrderById(restockOrderId);
		orderToCancel.setQuantityToRestock(0); // just for demonstration
		this.restockOrderRepository.insertRestockOrder(orderToCancel);
	}

	@Joining(condition = "d1.deviceCond", diagramIdentifiers = {"d1.id"})
	@Action(message = "d1.logOpResult", diagramIdentifiers = {"d1.id"}, actionType = ActionType.END)
	private void logCreateResponse(boolean isSuccess, String message) {
		if (isSuccess) {
			logger.debug("Restock order created successfully: " + message);
		} else {
			logger.error("Could not create restock order: " + message);
		}
	}
}
