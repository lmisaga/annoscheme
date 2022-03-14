package com.sclad.scladapp.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.sclad.scladapp.entity.DeviceType;

public class DeviceModel extends AbstractModel {

    @NotNull
    private String productName;

    @NotNull
    @NotBlank(message = "Product code needs to be filled!")
    private String productCode;

    @NotNull
    private Integer quantity;

    private Integer quantityThreshold = 1;

    private Boolean isReordered;

    private DeviceType deviceType;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantityThreshold() {
        return quantityThreshold;
    }

    public void setQuantityThreshold(Integer quantityThreshold) {
        this.quantityThreshold = quantityThreshold;
    }

    public Boolean getReordered() {
        return isReordered;
    }

    public void setReordered(Boolean reordered) {
        this.isReordered = reordered;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }
}
