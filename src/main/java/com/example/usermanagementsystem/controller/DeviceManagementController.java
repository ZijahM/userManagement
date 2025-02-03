package com.example.usermanagementsystem.controller;

import com.example.usermanagementsystem.dto.device.UserDeviceResponse;
import com.example.usermanagementsystem.service.DeviceManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceManagementController {

    private final DeviceManagementService deviceService;

    @GetMapping
    public ResponseEntity<List<UserDeviceResponse>> getUserDevices(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(deviceService.getUserDevices(extractUserId(userDetails)));
    }

    @PostMapping("/{deviceId}/logout")
    public ResponseEntity<Void> logoutDevice(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long deviceId
    ) {
        deviceService.logoutDevice(extractUserId(userDetails), deviceId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAllDevices(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long exceptDeviceId
    ) {
        deviceService.logoutAllDevices(extractUserId(userDetails), exceptDeviceId);
        return ResponseEntity.ok().build();
    }

    private Long extractUserId(UserDetails userDetails) {
        return ((com.example.usermanagementsystem.model.User) userDetails).getId();
    }
}
