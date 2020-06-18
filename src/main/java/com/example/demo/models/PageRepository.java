package com.example.demo.models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface PageRepository extends JpaRepository<Page , Integer> {
	
	public Page findBySlug(String slug);

	// First way
//	@Query("SELECT p FROM Page p WHERE p.id !=:id and p.slug = :slug")
//	public Page findBySlug(int id , String slug);
	
	// Second way
	public Page findBySlugAndIdNot(String slug , int id);
	
	public List<Page> findAllByOrderBySortingAsc();
}
