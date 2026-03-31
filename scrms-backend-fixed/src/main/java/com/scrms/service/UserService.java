package com.scrms.service;

import com.scrms.dto.request.UserUpdateRequest;
import com.scrms.dto.response.UserResponse;
import com.scrms.enums.Role;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    List<UserResponse> getUsersByRole(Role role);
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    void toggleUserStatus(Long id);
    void deleteUser(Long id);
}
