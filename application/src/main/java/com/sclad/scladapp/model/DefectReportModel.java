package com.sclad.scladapp.model;

import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import com.sclad.scladapp.entity.Device;

public class DefectReportModel extends AbstractModel {

    @NotNull
    private String deviceSerialNumber;

    private Device device;

    @NotNull
    private LocalDate dateOfDiscovery;

    private String faultDescription;

    @Nullable
    private Long attachmentId;

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public LocalDate getDateOfDiscovery() {
        return dateOfDiscovery;
    }

    public void setDateOfDiscovery(LocalDate dateOfDiscovery) {
        this.dateOfDiscovery = dateOfDiscovery;
    }

    public String getFaultDescription() {
        return faultDescription;
    }

    public void setFaultDescription(String faultDescription) {
        this.faultDescription = faultDescription;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }
}
