package com.sclad.scladapp.mapper;

import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.ActionType;
import org.springframework.stereotype.Component;

import com.sclad.scladapp.entity.Device;
import com.sclad.scladapp.model.DeviceModel;

@Component
public class DeviceMapper extends AbstractEntityMapper<Device, DeviceModel> {

	protected DeviceMapper() {
		super(Device.class, DeviceModel.class);
	}

	@Override
	@Action(actionType = ActionType.END, message = "device.details.mapToModel", parentMessage = "device.details.findById", diagramIdentifiers = {"device.details"})
	public DeviceModel toDto(Device device) {
		if (device.getId() == null || device.getProductCode() == null) {
			throw new IllegalStateException("Invalid device entity state");
		}
		return super.toDto(device);
	}
}
