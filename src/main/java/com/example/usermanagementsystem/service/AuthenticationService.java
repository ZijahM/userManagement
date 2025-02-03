package com.example.usermanagementsystem.service;

import com.example.usermanagementsystem.dto.auth.AuthenticationRequest;
import com.example.usermanagementsystem.dto.auth.AuthenticationResponse;
import com.example.usermanagementsystem.dto.auth.RegisterRequest;
import com.example.usermanagementsystem.model.Role;
import com.example.usermanagementsystem.model.User;
import com.example.usermanagementsystem.model.UserDevice;
import com.example.usermanagementsystem.repository.UserDeviceRepository;
import com.example.usermanagementsystem.repository.UserRepository;
import com.example.usermanagementsystem.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final UserDeviceRepository deviceRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Create user
        var user = User.builder()
                .username(request.getUsername() != null ? request.getUsername() : request.getEmail())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .emailVerified(true) // Set to true to skip email verification
                .build();
        
        user = userRepository.save(user);

        // Create user device
        String deviceName = request.getDeviceName() != null ? request.getDeviceName() : "Unknown Device";
        String deviceType = request.getDeviceType() != null ? request.getDeviceType() : "Unknown Type";
        
        var userDevice = createUserDevice(user, deviceName, deviceType, httpRequest.getRemoteAddr());
        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        userDevice.setRefreshToken(refreshToken);
        deviceRepository.save(userDevice);
        
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .deviceId(userDevice.getId())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest httpRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if device exists and is active
        String deviceName = request.getDeviceName() != null ? request.getDeviceName() : "Unknown Device";
        String deviceType = request.getDeviceType() != null ? request.getDeviceType() : "Unknown Type";
        
        var userDevice = createUserDevice(user, deviceName, deviceType, httpRequest.getRemoteAddr());
        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        userDevice.setRefreshToken(refreshToken);
        deviceRepository.save(userDevice);
        
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .deviceId(userDevice.getId())
                .build();
    }

    private UserDevice createUserDevice(User user, String deviceName, String deviceType, String ipAddress) {
        var refreshToken = jwtService.generateRefreshToken(user);
        var device = UserDevice.builder()
                .user(user)
                .deviceName(deviceName)
                .deviceType(deviceType)
                .ipAddress(ipAddress)
                .lastLoginTime(LocalDateTime.now())
                .active(true)
                .refreshToken(refreshToken)
                .build();
        
        return deviceRepository.save(device);
    }

    private void sendVerificationEmail(User user) {
        String verificationLink = "http://localhost:8080/api/v1/auth/verify?token=" + user.getVerificationToken();
        String emailContent = String.format(
                "Dear %s,\n\nPlease click the link below to verify your email:\n%s\n\nBest regards,\nYour App Team",
                user.getUsername(),
                verificationLink
        );
        emailService.sendEmail(user.getEmail(), "Email Verification", emailContent);
    }

    @Transactional
    public void verifyEmail(String token) {
        var user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));
        
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }
}
