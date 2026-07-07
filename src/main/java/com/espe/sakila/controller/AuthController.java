package com.espe.sakila.controller;

import com.espe.sakila.dto.UserDTO;
import com.espe.sakila.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Registro y autenticación de usuarios")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario con rol USER")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    /**
     * Login es manejado por Spring Security HTTP Basic.
     * Este endpoint existe solo para documentación en Swagger.
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión (HTTP Basic Auth)",
               description = "Envía username y password como Basic Auth en el header. " +
                             "Si las credenciales son correctas, los endpoints protegidos quedan disponibles.")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("Autenticación exitosa.");
    }
}
