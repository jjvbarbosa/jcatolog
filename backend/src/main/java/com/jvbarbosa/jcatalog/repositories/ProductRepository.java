package com.jvbarbosa.jcatalog.repositories;

import com.jvbarbosa.jcatalog.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
