package com.fixitnow.service;

import com.fixitnow.dto.UserDTO;
import com.fixitnow.exception.ResourceNotFoundException;
import com.fixitnow.model.User;
import com.fixitnow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> findUserById(Long id) {
        return userRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if (existsByEmail(userDTO.getEmail())) {
            throw new IllegalStateException("Email already in use");
        }
        
        User user = toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getActualPassword()));
        user.setEnabled(true);
        
        return toDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    modelMapper.map(userDTO, existingUser);
                    // Don't update password here, use changePassword method instead
                    existingUser.setEmail(userDTO.getEmail());
                    existingUser.setName(userDTO.getName());
                    existingUser.setRole(userDTO.getRole());
                    existingUser.setLocation(userDTO.getLocation());
                    return toDTO(userRepository.save(existingUser));
                })
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        String email;
        
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            email = (String) principal;
        } else {
            return null;
        }
        
        return userRepository.findByEmail(email)
                .map(this::toDTO)
                .orElse(null);
    }

    @Override
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        return modelMapper.map(userDTO, User.class);
    }
}
