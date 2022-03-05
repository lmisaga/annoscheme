package org.annoscheme.application.device;

import org.annoscheme.common.annotation.Action;

import java.util.Arrays;
import java.util.List;

public class DeviceRepository {

	private final List<Device> deviceStorageMock = Arrays.asList(
			new Device(1, "product1", 4, 12),
			new Device(2, "product2", 5, 20),
			new Device(3, "product3", 0, 10),
			new Device(4, "product4", 10, 11),
			new Device(5, "product5", 15, 15)
	);

	@Action(message = "d1.findById", parentMessage = "d1.start", diagramIdentifiers = {"d1.id"})
	public Device findDeviceById(Integer deviceId) {
		return deviceStorageMock
				.stream()
				.filter(device -> device.getId().equals(deviceId))
				.findFirst()
				.orElse(null);
	}

}
