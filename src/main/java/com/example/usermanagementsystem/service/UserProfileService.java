package com.example.usermanagementsystem.service;

import com.example.usermanagementsystem.dto.profile.UpdateProfileRequest;
import com.example.usermanagementsystem.dto.profile.UserProfileResponse;
import com.example.usermanagementsystem.model.User;
import com.example.usermanagementsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final String UPLOAD_DIR = "uploads/profile-images";

    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToUserProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean needsEmailVerification = false;

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            // Check if email is already taken
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already taken");
            }
            user.setEmail(request.getEmail());
            user.setEmailVerified(false);
            user.setVerificationToken(UUID.randomUUID().toString());
            needsEmailVerification = true;
        }

        if (request.getNewPassword() != null) {
            if (request.getCurrentPassword() == null || 
                !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new RuntimeException("Current password is incorrect");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        user = userRepository.save(user);

        if (needsEmailVerification) {
            sendVerificationEmail(user);
        }

        return mapToUserProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfileImage(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);

        // Save file
        Files.copy(file.getInputStream(), filePath);

        // Update user profile image URL
        user.setProfileImageUrl("/api/v1/profile/image/" + filename);
        user = userRepository.save(user);

        return mapToUserProfileResponse(user);
    }

    private UserProfileResponse mapToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .emailVerified(user.isEmailVerified())
                .build();
    }

    private void sendVerificationEmail(User user) {
        String verificationLink = "http://localhost:8080/api/v1/auth/verify?token=" + user.getVerificationToken();
        String emailContent = String.format(
                "Dear %s,\n\nPlease verify your new email address by clicking the link below:\n%s\n\nBest regards,\nYour App Team",
                user.getUsername(),
                verificationLink
        );
        emailService.sendEmail(user.getEmail(), "Email Verification", emailContent);
    }
}
