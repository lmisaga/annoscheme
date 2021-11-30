package org.annoscheme.application.restockOrder;

import org.annoscheme.application.common.AbstractEntity;

public class RestockOrder extends AbstractEntity {

	private Integer deviceId;

	private Integer quantityToRestock;

	public RestockOrder(Integer deviceId, Integer quantityToRestock) {
		this.deviceId = deviceId;
		this.quantityToRestock = quantityToRestock;
	}

	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public Integer getQuantityToRestock() {
		return quantityToRestock;
	}

	public void setQuantityToRestock(Integer quantityToRestock) {
		this.quantityToRestock = quantityToRestock;
	}
}
