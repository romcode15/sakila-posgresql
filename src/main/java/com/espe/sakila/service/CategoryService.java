package com.espe.sakila.service;

import com.espe.sakila.dto.CategoryDTO;
import com.espe.sakila.entity.Category;
import com.espe.sakila.exception.DuplicateEntityException;
import com.espe.sakila.exception.ResourceNotFoundException;
import com.espe.sakila.mapper.CategoryMapper;
import com.espe.sakila.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Integer id) {
        return categoryMapper.toDTO(getOrThrow(id));
    }

    @Transactional
    public CategoryDTO create(CategoryDTO dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new DuplicateEntityException("La categoría '" + dto.getName() + "' ya existe.");
        }
        return categoryMapper.toDTO(categoryRepository.save(categoryMapper.toEntity(dto)));
    }

    @Transactional
    public CategoryDTO update(Integer id, CategoryDTO dto) {
        Category category = getOrThrow(id);
        if (!category.getName().equalsIgnoreCase(dto.getName())
                && categoryRepository.existsByName(dto.getName())) {
            throw new DuplicateEntityException("Ya existe una categoría con el nombre '" + dto.getName() + "'.");
        }
        category.setName(dto.getName());
        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Integer id) {
        getOrThrow(id);
        categoryRepository.deleteById(id);
    }

    private Category getOrThrow(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
    }
}
