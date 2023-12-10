package com.decagon.ecommerce.repositories;

import com.decagon.ecommerce.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ProductRepositories extends JpaRepository<Product, Long>{
    List<Product> findByCategories(String categories);
}
