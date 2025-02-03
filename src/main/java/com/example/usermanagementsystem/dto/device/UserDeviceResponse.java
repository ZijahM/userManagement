package com.example.usermanagementsystem.dto.device;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDeviceResponse {
    private Long id;
    private String deviceName;
    private String deviceType;
    private String ipAddress;
    private LocalDateTime lastLoginTime;
    private boolean active;
}
