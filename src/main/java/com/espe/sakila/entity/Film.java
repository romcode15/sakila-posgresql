package com.espe.sakila.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "film")
@Data
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "film_id")
    private Integer filmId;

    @Column(nullable = false)
    private String title;

    private String description;

    /**
     * En Pagila, release_year es un DOMAIN personalizado sobre integer.
     * columnDefinition evita que Hibernate valide el tipo en ddl-auto=validate.
     */
    @Column(name = "release_year", columnDefinition = "year")
    private Integer releaseYear;

    @Column(name = "rental_duration")
    private Short rentalDuration;

    @Column(name = "rental_rate")
    private BigDecimal rentalRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}
