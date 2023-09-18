package com.jvbarbosa.jcatalog.services;

import com.jvbarbosa.jcatalog.dto.CategoryDTO;
import com.jvbarbosa.jcatalog.entities.Category;
import com.jvbarbosa.jcatalog.repositories.CategoryRepository;
import com.jvbarbosa.jcatalog.services.exceptions.DatabaseException;
import com.jvbarbosa.jcatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Category entity = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource with ID '" + id + "' not found"));
        return new CategoryDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(x -> new CategoryDTO(x));
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        Category entity = new Category(null, dto.getName());
        return new CategoryDTO(repository.save(entity));
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        try {
            Category entity = repository.getReferenceById(id);
            entity.setName(dto.getName());
            return new CategoryDTO(repository.save(entity));
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Resource with ID '" + id + "' not found");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Resource with ID '" + id + "' not found");
        }
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Referential Integrity Violation");
        }
    }
}
