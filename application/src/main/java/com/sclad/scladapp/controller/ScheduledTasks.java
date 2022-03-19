package com.sclad.scladapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.sclad.scladapp.entity.Device;
import com.sclad.scladapp.entity.RestockOrder;
import com.sclad.scladapp.repository.DeviceRepository;
import com.sclad.scladapp.repository.RestockOrderRepository;

@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private final DeviceRepository deviceRepository;
    private final RestockOrderRepository restockOrderRepository;

    @Autowired
    public ScheduledTasks(DeviceRepository deviceRepository, RestockOrderRepository restockOrderRepository) {
        this.deviceRepository = deviceRepository;
        this.restockOrderRepository = restockOrderRepository;
    }

    @Scheduled(fixedDelayString = "${scheduledTask.taskFixedDelay}")
    public void simulateStockManipulation() {
        logger.info("Executing SimulatedStockManipulation scheduled task");
        List<RestockOrder> restockOrders = restockOrderRepository.findAll();
        for (RestockOrder restockOrder : restockOrders) {
            Optional<Device> restockedDevice = deviceRepository.findById(restockOrder.getDevice().getId());
            if (restockedDevice.isPresent()) {
                Device device = restockedDevice.get();
                device.setQuantity(device.getQuantity() + restockOrder.getQuantityToReorder());
                if (Boolean.TRUE.equals(restockOrder.getSendNotification())) {
                    sendRestockedNotification(device.getProductCode(), device.getProductName());
                }
                device.setReordered(Boolean.FALSE);
                restockOrderRepository.delete(restockOrder);
                deviceRepository.save(device);
            } else {
                logger.error("Could not resolve restock order with ID = " + restockOrder.getId());
            }
        }
        List<Device> updatedDevices = deviceRepository.findAll();
        updatedDevices.stream().filter(device -> !Boolean.TRUE.equals(device.getReordered())).forEach(device -> {
            if (device.getQuantity() >= device.getQuantityThreshold()) {
                Random random = new Random();
                Integer randomQuantity = device.getQuantityThreshold() > 1 ? Math.abs(random.nextInt()) % device.getQuantityThreshold() : 1;
                device.setQuantity(Math.abs(device.getQuantity() - randomQuantity));
            }
        });

        deviceRepository.saveAll(updatedDevices);
        sendLowStockNotification(updatedDevices);
		logger.info("SimulatedStockManipulation scheduled task finished successfully");
    }

    //PUSH notifications using Firebase [sendLowStockNotification, sendRestockedNotification] - TBD

    private void sendLowStockNotification(List<Device> devices) {
        devices.forEach(device -> {
            if (device.getQuantity() < device.getQuantityThreshold()) {
                logger.info("Device " + device.getProductCode() + " " + device.getProductName() + " has low stocks.");
            }
        });
    }

    private void sendRestockedNotification(String productCode, String productName) {
        logger.info("Device " + productCode + " " + productName + " has been restocked");
    }
}
