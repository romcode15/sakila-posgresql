package com.espe.sakila.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class RentalRequestDTO {

    @NotEmpty(message = "Debe incluir al menos una película")
    @Valid
    private List<RentalItemDTO> items;

    @Data
    public static class RentalItemDTO {

        @NotNull(message = "El filmId es obligatorio")
        private Integer filmId;

        @Min(value = 1, message = "La cantidad mínima es 1")
        private Integer quantity;
    }
}
