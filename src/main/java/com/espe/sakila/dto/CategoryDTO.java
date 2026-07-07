package com.espe.sakila.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDTO {

    private Integer categoryId;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String name;
}
