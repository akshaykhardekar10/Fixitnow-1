package com.fixitnow.service;

import com.fixitnow.dto.UserDTO;
import com.fixitnow.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO> findAllUsers();
    Optional<UserDTO> findUserById(Long id);
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    boolean existsByEmail(String email);
    UserDTO getCurrentUser();
    UserDTO toDTO(User user);
    User toEntity(UserDTO userDTO);
}
