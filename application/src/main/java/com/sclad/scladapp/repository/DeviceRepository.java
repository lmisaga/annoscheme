package com.sclad.scladapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.sclad.scladapp.entity.Device;
import com.sclad.scladapp.entity.DeviceType;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    List<Device> findByDeviceType(DeviceType deviceType);
}

