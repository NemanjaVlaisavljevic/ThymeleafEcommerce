package com.example.demo.models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category , Integer> {

	public Category findByName(String name);
	
	public List<Category> findAllByOrderBySortingAsc();

	public Category findBySlug(String slug);
	
}
