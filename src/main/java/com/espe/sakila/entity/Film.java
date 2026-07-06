package com.espe.sakila.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "film")
@Data
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long filmId;
    
    @Column(nullable = false)
    private String title;
    
    private String description;
    private Integer releaseYear;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    private BigDecimal rentalRate;
}