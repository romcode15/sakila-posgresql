package com.espe.sakila.service;

import com.espe.sakila.dto.UserDTO;
import com.espe.sakila.entity.Role;
import com.espe.sakila.entity.User;
import com.espe.sakila.exception.DuplicateEntityException;
import com.espe.sakila.exception.ResourceNotFoundException;
import com.espe.sakila.repository.RoleRepository;
import com.espe.sakila.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO register(UserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateEntityException("El username '" + dto.getUsername() + "' ya está en uso.");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rol USER no encontrado. Asegúrese de que el sistema fue inicializado."));

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(userRole);
        user.setActive(true);
        user = userRepository.save(user);

        UserDTO response = new UserDTO();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        return response;
    }
}
