package com.example.usermanagementsystem.repository;

import com.example.usermanagementsystem.model.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    List<UserDevice> findByUserIdAndActiveTrue(Long userId);
    Optional<UserDevice> findByRefreshToken(String refreshToken);
}
