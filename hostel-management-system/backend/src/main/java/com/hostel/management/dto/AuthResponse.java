package com.hostel.management.dto;

import com.hostel.management.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long studentId;
    private String name;
    private Role role;
}
