package com.example.usermanagementsystem.dto.profile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String profileImageUrl;
    private boolean emailVerified;
}
