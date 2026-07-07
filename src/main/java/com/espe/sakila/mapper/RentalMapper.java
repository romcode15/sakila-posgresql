package com.espe.sakila.mapper;

import com.espe.sakila.dto.RentalResponseDTO;
import com.espe.sakila.entity.Rental;
import org.springframework.stereotype.Component;

@Component
public class RentalMapper {

    public RentalResponseDTO toDTO(Rental rental) {
        RentalResponseDTO dto = new RentalResponseDTO();
        dto.setRentalId(rental.getRentalId());
        dto.setFilmTitle(rental.getInventory().getFilm().getTitle());
        dto.setRentalDate(rental.getRentalDate());
        dto.setReturnDate(rental.getReturnDate());
        dto.setStatus(rental.getReturnDate() == null ? "ACTIVO" : "DEVUELTO");
        return dto;
    }
}
