package com.authentication.auth_service.dto;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}
