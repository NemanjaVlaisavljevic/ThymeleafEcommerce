package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.models.Category;
import com.example.demo.models.CategoryRepository;
import com.example.demo.models.Product;
import com.example.demo.models.ProductRepository;

@Controller
@RequestMapping("/category")
public class CategoriesController {

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@GetMapping("/{slug}")
	public String index(@PathVariable String slug , Model theModel , @RequestParam(value = "page" , required = false) Integer p) {
		
		int perPage = 6;
		int page = (p != null) ? p : 0;
		
		Pageable pageable = PageRequest.of(page, perPage);
		
		long count = 0;
		
		if(slug.equals("all")) {
			Page<Product> products = productRepository.findAll(pageable);
			count = productRepository.count();
			
			theModel.addAttribute("products" , products);
		}else {
			Category category = categoryRepository.findBySlug(slug);
			
			if(category == null) {
				return "redirect:/";
			}
			
			int categoryId = category.getId();
			String categoryName = category.getName();
			
			List<Product> products = productRepository.findAllByCategoryId(Integer.toString(categoryId), pageable);
			
			count = productRepository.countByCategoryId(Integer.toString(categoryId));
			
			theModel.addAttribute("products" , products);
			theModel.addAttribute("categoryName" , categoryName);
		}
		
		double pageCount = Math.ceil((double) count / (double) perPage);
		
		theModel.addAttribute("pageCount" , (int) pageCount);
		theModel.addAttribute("perPage" , perPage);
		theModel.addAttribute("count" , count);
		theModel.addAttribute("page" , page);
		
		return "products";
	}
}
