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

	@Action(message = "Find device by ID",
			parentMessage = "Parse request",
			diagramIdentifiers = {"1"})
	public Device findDeviceById(Integer deviceId) {
		return deviceStorageMock.stream()
								.filter(d -> d.getId().equals(deviceId))
								.findFirst()
								.orElse(null);
	}

}
