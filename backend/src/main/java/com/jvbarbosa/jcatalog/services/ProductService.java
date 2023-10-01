package com.jvbarbosa.jcatalog.services;

import com.jvbarbosa.jcatalog.dto.ProductDTO;
import com.jvbarbosa.jcatalog.entities.Category;
import com.jvbarbosa.jcatalog.entities.Product;
import com.jvbarbosa.jcatalog.projections.ProductProjection;
import com.jvbarbosa.jcatalog.repositories.CategoryRepository;
import com.jvbarbosa.jcatalog.repositories.ProductRepository;
import com.jvbarbosa.jcatalog.services.exceptions.DatabaseException;
import com.jvbarbosa.jcatalog.services.exceptions.ResourceNotFoundException;
import com.jvbarbosa.jcatalog.utils.Util;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product entity = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource with ID '" + id + "' not found"));
        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable, String name, String categoriesId) {

        List<Long> categories = Arrays.asList();
        if (!"0".equals(categoriesId)) {
            categories = Arrays.asList(categoriesId.split(",")).stream().map(x -> Long.parseLong(x)).toList();
        }

        Page<ProductProjection> page = repository.searchProducts(pageable, name, categories);
        List<Long> productIds = page.map(x -> x.getId()).toList();

        List<Product> entities = repository.searchProductsWithCategories(productIds);
        entities = (List<Product>) Util.replace(page.getContent(), entities);

        List<ProductDTO> dtos = entities.stream().map(p -> new ProductDTO(p, p.getCategories())).toList();

        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product entity = new Product();
        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);

        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = repository.save(entity);

            return new ProductDTO(entity);
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

    private void copyDtoToEntity(ProductDTO dto, Product entity) {

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setDate(dto.getDate());
        entity.setImgUrl(dto.getImgUrl());
        entity.setPrice(dto.getPrice());

        entity.getCategories().clear();
        dto.getCategories().forEach(catDto -> {
            Category category = categoryRepository.getReferenceById(catDto.getId());
            entity.getCategories().add(category);
        });
    }
}
