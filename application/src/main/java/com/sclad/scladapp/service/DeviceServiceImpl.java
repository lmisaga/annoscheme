package com.sclad.scladapp.service;

import org.annoscheme.common.annotation.Action;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

import com.sclad.scladapp.entity.Device;
import com.sclad.scladapp.entity.DeviceType;
import com.sclad.scladapp.exceptions.CategoryNotFoundException;
import com.sclad.scladapp.exceptions.DeviceCouldNotBeDeletedException;
import com.sclad.scladapp.exceptions.DeviceNotFoundException;
import com.sclad.scladapp.mapper.DeviceMapper;
import com.sclad.scladapp.model.DeviceModel;
import com.sclad.scladapp.repository.DeviceRepository;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
	private final DeviceMapper deviceMapper;

    public DeviceServiceImpl(DeviceRepository deviceRepository, DeviceMapper deviceMapper) {
        this.deviceRepository = deviceRepository;
		this.deviceMapper = deviceMapper;
    }

    @Override
    public Device create(DeviceModel model) {
        Device device = new Device();
        device.setProductName(model.getProductName());
        device.setProductCode(model.getProductCode());
        device.setQuantity(model.getQuantity());
        device.setQuantityThreshold(model.getQuantityThreshold());
        device.setReordered(Boolean.TRUE.equals(model.getReordered()));
        device.setDeviceType(model.getDeviceType());
        deviceRepository.save(device);
        return device;
    }

	@Override
  	public DeviceModel getDeviceDetail(Long id) {
		Device device = this.getById(id);
		return deviceMapper.toDto(device);
	}

	@Action(message = "device.details.findById", parentMessage = "device.details.receiveRequest", diagramIdentifiers = {"device.details"})
    public Device getById(Long id) {
		return deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));
    }

    @Override
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    @Override
    public List<Device> listAllDevicesByType(String deviceType) {
        DeviceType type;
        try {
            type = DeviceType.valueOf(deviceType);
        } catch (IllegalArgumentException e) {
            throw new CategoryNotFoundException();
        }
        return deviceRepository.findByDeviceType(type);
    }

    @Override
    public Device updateDevice(DeviceModel updatedModel, Long id) {
        Device device;
        device = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));

        device.setProductName(updatedModel.getProductName());
        device.setProductCode(updatedModel.getProductCode());
        device.setDeviceType(updatedModel.getDeviceType());
        device.setQuantity(updatedModel.getQuantity());
        device.setQuantityThreshold(updatedModel.getQuantityThreshold());
        device.setReordered(updatedModel.getReordered());
        deviceRepository.save(device);
        return device;
    }

    @Override
    public void deleteDevice(Long id) {
        try {
            deviceRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new DeviceNotFoundException(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DeviceCouldNotBeDeletedException();
        }

    }
}
