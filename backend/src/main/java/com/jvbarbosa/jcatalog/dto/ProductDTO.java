package com.jvbarbosa.jcatalog.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.jvbarbosa.jcatalog.entities.Category;
import com.jvbarbosa.jcatalog.entities.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class ProductDTO {

    private Long id;

    @Size(min = 5, max = 60, message = "Name must be between 5 and 60 characters")
    @NotBlank(message = "Required field")
    private String name;
    private String description;
    @Positive(message = "Price must be positive")
    private Double price;
    private String imgUrl;
    @PastOrPresent(message = "Product date cannot be in the future")
    private Instant date;
    private Set<CategoryDTO> categories = new HashSet<>();

    @JsonCreator
    public ProductDTO(Long id, String name, String description, Double price, String imgUrl, Instant date) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
        this.date = date;
    }

    public ProductDTO(Product entity) {
        id = entity.getId();
        name = entity.getName();
        description = entity.getDescription();
        price = entity.getPrice();
        imgUrl = entity.getImgUrl();
        date = entity.getDate();
    }

    public ProductDTO(Product entity, Set<Category> categories) {
        this(entity);
        categories.forEach(cat -> this.categories.add(new CategoryDTO(cat)));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public Instant getDate() {
        return date;
    }

    public Set<CategoryDTO> getCategories() {
        return categories;
    }
}
