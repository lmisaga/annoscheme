package com.sclad.scladapp.controller;

import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.sclad.scladapp.entity.Device;
import com.sclad.scladapp.model.DeviceModel;
import com.sclad.scladapp.service.DeviceService;

@RestController
@RequestMapping(value = "/api/device")
public class DeviceController {

    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Device create(@RequestBody DeviceModel model) {
        return deviceService.create(model);
    }

	@Action(actionType = ActionType.START, message = "device.details.receiveRequest", diagramIdentifiers = {"device.details"})
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public DeviceModel getDeviceDetail(@PathVariable Long id) {
        return deviceService.getDeviceDetail(id);
    }

//	@Action(actionType = ActionType.END, message = "d1.saveResOr", diagramIdentifiers = {"d1.id"}, parentMessage = "d1.createResOr")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<Device> getAllDevices() {
        return deviceService.getAllDevices();
    }

    @RequestMapping(value = "/listAllDevicesByType/{deviceType}", method = RequestMethod.GET)
    public List<Device> listAllDevicesByCategory(@PathVariable String deviceType) {
        return deviceService.listAllDevicesByType(deviceType);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public Device updateDevice(@RequestBody DeviceModel updatedModel, @PathVariable Long id) {
        return deviceService.updateDevice(updatedModel, id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public void deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
    }
}
