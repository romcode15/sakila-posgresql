package com.espe.sakila.dto;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class RentalResponseDTO {
    private Integer rentalId;
    private String filmTitle;
    private OffsetDateTime rentalDate;
    private OffsetDateTime returnDate;
    private String status;
}
