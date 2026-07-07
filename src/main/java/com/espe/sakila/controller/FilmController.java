package com.espe.sakila.controller;

import com.espe.sakila.dto.FilmDTO;
import com.espe.sakila.service.FilmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Películas", description = "Listado público + CRUD admin + gestión de inventario")
public class FilmController {

    @Autowired
    private FilmService filmService;

    @GetMapping("/api/films")
    @Operation(summary = "Listar todas las películas con disponibilidad")
    public ResponseEntity<List<FilmDTO>> findAll() {
        return ResponseEntity.ok(filmService.findAll());
    }

    @GetMapping("/api/films/{id}")
    @Operation(summary = "Ver detalle de una película con disponibilidad")
    public ResponseEntity<FilmDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(filmService.findById(id));
    }

    @PostMapping("/api/admin/films")
    @Operation(summary = "Crear película con inventario inicial (ADMIN)")
    public ResponseEntity<FilmDTO> create(@Valid @RequestBody FilmDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(filmService.create(dto));
    }

    @PutMapping("/api/admin/films/{id}")
    @Operation(summary = "Actualizar película y/o cambiar categoría (ADMIN)")
    public ResponseEntity<FilmDTO> update(@PathVariable Integer id, @Valid @RequestBody FilmDTO dto) {
        return ResponseEntity.ok(filmService.update(id, dto));
    }

    @DeleteMapping("/api/admin/films/{id}")
    @Operation(summary = "Eliminar película (ADMIN)")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        filmService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/admin/films/{id}/inventory")
    @Operation(summary = "Agregar unidades de inventario a una película (ADMIN)")
    public ResponseEntity<FilmDTO> addInventory(
            @PathVariable Integer id,
            @RequestParam @Min(1) int quantity) {
        return ResponseEntity.ok(filmService.addInventory(id, quantity));
    }
}
