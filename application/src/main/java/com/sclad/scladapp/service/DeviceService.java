package com.sclad.scladapp.service;

import java.util.List;

import com.sclad.scladapp.entity.Device;
import com.sclad.scladapp.model.DeviceModel;

public interface DeviceService extends AbstractService<Device> {

    Device create(DeviceModel model);

    List<Device> getAllDevices();

    List<Device> listAllDevicesByType(String deviceType);

    Device updateDevice(DeviceModel updatedModel, Long id);

	DeviceModel getDeviceDetail(Long id);

    void deleteDevice(Long id);
}
