package com.example.demo.models;

import org.springframework.data.domain.Pageable;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	Product findBySlug(String slug);

	Product findBySlugAndIdNot(String slug, int id);
	
	Page<Product> findAll(Pageable pageable);

	List<Product> findAllByCategoryId(String categoryId, Pageable pageable);

	long countByCategoryId(String categoryId);
		
	

}
