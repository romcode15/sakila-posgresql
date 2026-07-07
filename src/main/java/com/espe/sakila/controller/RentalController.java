package com.espe.sakila.controller;

import com.espe.sakila.dto.RentalRequestDTO;
import com.espe.sakila.dto.RentalResponseDTO;
import com.espe.sakila.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@Tag(name = "Rentals", description = "Alquiler, historial y devolución de películas")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @PostMapping("/api/rentals")
    @Operation(summary = "Rentar una o varias películas (transaccional con rollback completo)")
    public ResponseEntity<List<RentalResponseDTO>> rent(
            @Valid @RequestBody RentalRequestDTO request,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rentalService.createRental(principal.getName(), request));
    }

    @GetMapping("/api/rentals/active")
    @Operation(summary = "Ver mis alquileres activos")
    public ResponseEntity<List<RentalResponseDTO>> myActive(Principal principal) {
        return ResponseEntity.ok(rentalService.getActiveRentals(principal.getName()));
    }

    @GetMapping("/api/rentals/history")
    @Operation(summary = "Ver historial completo de mis alquileres")
    public ResponseEntity<List<RentalResponseDTO>> myHistory(Principal principal) {
        return ResponseEntity.ok(rentalService.getRentalHistory(principal.getName()));
    }

    @PatchMapping("/api/rentals/{id}/return")
    @Operation(summary = "Devolver una película (solo el dueño del rental)")
    public ResponseEntity<RentalResponseDTO> returnRental(
            @PathVariable Integer id,
            Principal principal) {
        return ResponseEntity.ok(rentalService.returnRental(id, principal.getName()));
    }

    @GetMapping("/api/admin/rentals")
    @Operation(summary = "Ver todos los rentals del sistema (ADMIN)")
    public ResponseEntity<List<RentalResponseDTO>> allRentals() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    @GetMapping("/api/admin/rentals/active")
    @Operation(summary = "Ver todos los rentals activos del sistema (ADMIN)")
    public ResponseEntity<List<RentalResponseDTO>> allActive() {
        return ResponseEntity.ok(rentalService.getAllActiveRentals());
    }
}
