package org.annoscheme.application.restockOrder;

public class RestockOrderRequestModel {

	private Integer deviceId;

	private Integer restockQuantity;

	public RestockOrderRequestModel(Integer deviceId, Integer restockQuantity) {
		this.deviceId = deviceId;
		this.restockQuantity = restockQuantity;
	}

	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public Integer getRestockQuantity() {
		return restockQuantity;
	}

	public void setRestockQuantity(Integer restockQuantity) {
		this.restockQuantity = restockQuantity;
	}
}
