package com.espe.sakila.service;

import com.espe.sakila.dto.FilmDTO;
import com.espe.sakila.entity.Category;
import com.espe.sakila.entity.Film;
import com.espe.sakila.entity.Inventory;
import com.espe.sakila.exception.ResourceNotFoundException;
import com.espe.sakila.mapper.FilmMapper;
import com.espe.sakila.repository.CategoryRepository;
import com.espe.sakila.repository.FilmRepository;
import com.espe.sakila.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private FilmMapper filmMapper;

    @Transactional(readOnly = true)
    public List<FilmDTO> findAll() {
        return filmRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FilmDTO findById(Integer id) {
        return toDTO(getOrThrow(id));
    }

    /**
     * Crea película + categoría asociada + inventario inicial en un solo bloque transaccional.
     */
    @Transactional
    public FilmDTO create(FilmDTO dto) {
        Category category = getCategoryOrThrow(dto.getCategoryId());
        Film film = filmMapper.toEntity(dto, category);
        film = filmRepository.save(film);

        int stock = dto.getInitialStock() != null ? dto.getInitialStock() : 0;
        for (int i = 0; i < stock; i++) {
            Inventory inv = new Inventory();
            inv.setFilm(film);
            inv.setStoreId(1); // store_id requerido por el schema; valor por defecto
            inventoryRepository.save(inv);
        }

        return filmMapper.toDTO(film, stock);
    }

    /**
     * Actualiza película y cambia de categoría si se solicita — transaccional.
     */
    @Transactional
    public FilmDTO update(Integer id, FilmDTO dto) {
        Film film = getOrThrow(id);
        Category category = getCategoryOrThrow(dto.getCategoryId());

        film.setTitle(dto.getTitle());
        film.setDescription(dto.getDescription());
        film.setReleaseYear(dto.getReleaseYear());
        film.setRentalRate(dto.getRentalRate());
        if (dto.getRentalDuration() != null) {
            film.setRentalDuration(dto.getRentalDuration().shortValue());
        }
        film.setCategory(category);

        film = filmRepository.save(film);
        return toDTO(film);
    }

    @Transactional
    public void delete(Integer id) {
        getOrThrow(id);
        filmRepository.deleteById(id);
    }

    /**
     * Agrega registros de inventario adicionales a una película existente.
     */
    @Transactional
    public FilmDTO addInventory(Integer filmId, int quantity) {
        Film film = getOrThrow(filmId);
        for (int i = 0; i < quantity; i++) {
            Inventory inv = new Inventory();
            inv.setFilm(film);
            inv.setStoreId(1);
            inventoryRepository.save(inv);
        }
        return toDTO(film);
    }

    // --- helpers ---

    private FilmDTO toDTO(Film film) {
        long total = inventoryRepository.countByFilmFilmId(film.getFilmId());
        long active = inventoryRepository.countActiveRentalsByFilmId(film.getFilmId());
        return filmMapper.toDTO(film, total - active);
    }

    private Film getOrThrow(Integer id) {
        return filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Película no encontrada con id: " + id));
    }

    private Category getCategoryOrThrow(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + categoryId));
    }
}
