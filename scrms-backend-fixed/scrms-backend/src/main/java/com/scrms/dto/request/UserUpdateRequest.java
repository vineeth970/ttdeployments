package com.scrms.dto.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String fullName;
    private String department;
    private String phone;
    private Boolean active;
}
