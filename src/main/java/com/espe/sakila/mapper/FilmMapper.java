package com.espe.sakila.mapper;

import com.espe.sakila.dto.FilmDTO;
import com.espe.sakila.entity.Category;
import com.espe.sakila.entity.Film;
import org.springframework.stereotype.Component;

@Component
public class FilmMapper {

    public Film toEntity(FilmDTO dto, Category category) {
        Film film = new Film();
        film.setTitle(dto.getTitle());
        film.setDescription(dto.getDescription());
        film.setReleaseYear(dto.getReleaseYear());
        film.setRentalRate(dto.getRentalRate());
        film.setRentalDuration(dto.getRentalDuration() != null ? dto.getRentalDuration().shortValue() : 3);
        film.setCategory(category);
        return film;
    }

    public FilmDTO toDTO(Film film, long availableStock) {
        FilmDTO dto = new FilmDTO();
        dto.setFilmId(film.getFilmId());
        dto.setTitle(film.getTitle());
        dto.setDescription(film.getDescription());
        dto.setReleaseYear(film.getReleaseYear());
        dto.setRentalRate(film.getRentalRate());
        dto.setRentalDuration(film.getRentalDuration() != null ? film.getRentalDuration().intValue() : null);
        dto.setCategoryId(film.getCategory() != null ? film.getCategory().getCategoryId() : null);
        dto.setCategoryName(film.getCategory() != null ? film.getCategory().getName() : null);
        dto.setAvailableStock(availableStock);
        return dto;
    }
}
