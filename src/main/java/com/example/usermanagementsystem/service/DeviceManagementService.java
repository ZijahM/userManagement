package com.example.usermanagementsystem.service;

import com.example.usermanagementsystem.dto.device.UserDeviceResponse;
import com.example.usermanagementsystem.model.UserDevice;
import com.example.usermanagementsystem.repository.UserDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceManagementService {
    private final UserDeviceRepository deviceRepository;

    public List<UserDeviceResponse> getUserDevices(Long userId) {
        return deviceRepository.findByUserIdAndActiveTrue(userId)
                .stream()
                .map(this::mapToUserDeviceResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void logoutDevice(Long userId, Long deviceId) {
        UserDevice device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        if (!device.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to device");
        }

        device.setActive(false);
        deviceRepository.save(device);
    }

    @Transactional
    public void logoutAllDevices(Long userId, Long exceptDeviceId) {
        List<UserDevice> devices = deviceRepository.findByUserIdAndActiveTrue(userId);
        devices.stream()
                .filter(device -> !device.getId().equals(exceptDeviceId))
                .forEach(device -> {
                    device.setActive(false);
                    deviceRepository.save(device);
                });
    }

    private UserDeviceResponse mapToUserDeviceResponse(UserDevice device) {
        return UserDeviceResponse.builder()
                .id(device.getId())
                .deviceName(device.getDeviceName())
                .deviceType(device.getDeviceType())
                .ipAddress(device.getIpAddress())
                .lastLoginTime(device.getLastLoginTime())
                .active(device.isActive())
                .build();
    }
}
