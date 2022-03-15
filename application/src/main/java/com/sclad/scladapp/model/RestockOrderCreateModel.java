package com.sclad.scladapp.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class RestockOrderCreateModel {

	@NotNull
	@Min(1)
	private Integer quantityToReorder;

	private Boolean sendNotification;

	@NotNull
	@Min(0)
	private Long deviceId;

	public Integer getQuantityToReorder() {
		return quantityToReorder;
	}

	public void setQuantityToReorder(Integer quantityToReorder) {
		this.quantityToReorder = quantityToReorder;
	}

	public Boolean getSendNotification() {
		return sendNotification;
	}

	public void setSendNotification(Boolean sendNotification) {
		this.sendNotification = sendNotification;
	}

	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}
}
