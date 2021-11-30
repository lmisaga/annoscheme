package org.annoscheme.application.device;

import org.annoscheme.application.common.AbstractEntity;

public class Device extends AbstractEntity {

	private String productNumber;

	private Integer currentQuantity;

	private Integer quantityThreshold;

	public Device(Integer id, String productNumber, Integer currentQuantity, Integer quantityThreshold) {
		this.id = id;
		this.productNumber = productNumber;
		this.currentQuantity = currentQuantity;
		this.quantityThreshold = quantityThreshold;
	}

	public String getProductNumber() {
		return productNumber;
	}

	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}

	public Integer getCurrentQuantity() {
		return currentQuantity;
	}

	public void setCurrentQuantity(Integer currentQuantity) {
		this.currentQuantity = currentQuantity;
	}

	public Integer getQuantityThreshold() {
		return quantityThreshold;
	}

	public void setQuantityThreshold(Integer quantityThreshold) {
		this.quantityThreshold = quantityThreshold;
	}
}
