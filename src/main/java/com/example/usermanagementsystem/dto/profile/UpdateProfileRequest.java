package com.example.usermanagementsystem.dto.profile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Email(message = "Invalid email format")
    private String email;

    private String currentPassword;
    
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;
}
