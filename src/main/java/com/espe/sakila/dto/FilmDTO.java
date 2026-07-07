package com.espe.sakila.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class FilmDTO {

    private Integer filmId;

    @NotBlank(message = "El título es obligatorio")
    private String title;

    private String description;
    private Integer releaseYear;

    @NotNull(message = "La categoría es obligatoria")
    private Integer categoryId;

    private String categoryName;

    @NotNull(message = "La tarifa de alquiler es obligatoria")
    @Positive
    private BigDecimal rentalRate;

    private Integer rentalDuration;

    /** Solo para creación: cuántos registros de inventario crear */
    @Positive
    private Integer initialStock;

    /** Calculado dinámicamente: inventario total - rentals activos */
    private Long availableStock;
}
