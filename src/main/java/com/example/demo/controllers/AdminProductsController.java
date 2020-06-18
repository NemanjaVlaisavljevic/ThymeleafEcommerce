package com.example.demo.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.models.Category;
import com.example.demo.models.CategoryRepository;
import com.example.demo.models.Product;
import com.example.demo.models.ProductRepository;

@Controller
@RequestMapping("/admin/products")
public class AdminProductsController {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@GetMapping
	public String index(Model theModel , @RequestParam(value = "page" , required = false) Integer p) {
		
		int perPage = 6;
		int page = (p != null) ? p : 0;
		
		Pageable pageable = PageRequest.of(page, perPage);
		
		Page<Product> products = productRepository.findAll(pageable);
		
		List<Category> categories = categoryRepository.findAll();

		HashMap<Integer,  String> cats = new HashMap<>();
		for(Category cat : categories) {
			cats.put(cat.getId(), cat.getName());
		}
		
		theModel.addAttribute("cats" , cats);
		theModel.addAttribute("products" , products);
		
		long count = productRepository.count();
		double pageCount = Math.ceil((double) count / (double) perPage);
		
		theModel.addAttribute("pageCount" , (int) pageCount);
		theModel.addAttribute("perPage" , perPage);
		theModel.addAttribute("count" , count);
		theModel.addAttribute("page" , page);
		
		return "admin/products/index";
	}
	
	@GetMapping("/add")
	public String add(Product product , Model theModel) {
		
		List<Category> categories = categoryRepository.findAll();
		
		theModel.addAttribute("categories" , categories);
		
		
		return "/admin/products/add";
	}
	
	@PostMapping("/add")
	public String add(@Valid Product product , 
		MultipartFile file , BindingResult bindingResult ,
		RedirectAttributes redirectAttribute , Model theModel) throws IOException {
		
		List<Category> categories = categoryRepository.findAll();
		
		
		if(bindingResult.hasErrors()) {
			theModel.addAttribute("categories" , categories);
			return "admin/products/add";
		}
	
		boolean fileOk = false;
		
		String fileName = file.getOriginalFilename();
		Path path = Paths.get("src/main/resources/static/media/" + fileName);
		byte[] bytes = file.getBytes();
		
		if(fileName.endsWith("png") || fileName.endsWith("jpg")) {
			fileOk = true;
		}
		
		redirectAttribute.addFlashAttribute("message" , "Product added");
		redirectAttribute.addFlashAttribute("alertClass" , "alert-success");
		
		String slug = product.getName().toLowerCase().replace(" ", "-");
		
		Product tempProduct = productRepository.findBySlug(slug);
		
		if(!fileOk) {
			redirectAttribute.addFlashAttribute("message" , "File must be JPG or PNG");
			redirectAttribute.addFlashAttribute("alertClass" , "alert-danger");
			redirectAttribute.addFlashAttribute("product" , product);
			
		}else if(tempProduct != null) {
			redirectAttribute.addFlashAttribute("message" , "Product already exists");
			redirectAttribute.addFlashAttribute("alertClass" , "alert-danger");
			redirectAttribute.addFlashAttribute("product" , product);
		}else {
			product.setSlug(slug);
			product.setImage(fileName);
			productRepository.save(product);
			
			Files.write(path ,  bytes);
		}
		return "redirect:/admin/products/add";
	}
	
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable int id, Model theModel) {
		
		List<Category> categories = categoryRepository.findAll();
		Product product = productRepository.getOne(id);
		
		theModel.addAttribute("product" , product);
		theModel.addAttribute("categories" , categories);
		
		return "admin/products/edit";
	}
	
	@PostMapping("/edit")
	public String edit(@Valid Product product , 
		  BindingResult bindingResult ,MultipartFile file,
		RedirectAttributes redirectAttribute , Model theModel) throws IOException {
		
		Product currentProduct = productRepository.getOne(product.getId());
		
		List<Category> categories = categoryRepository.findAll();
		
		
		if(bindingResult.hasErrors()) {
			theModel.addAttribute("productName" , currentProduct.getName());
			theModel.addAttribute("categories" , categories);
			return "admin/products/edit";
		}
	
		boolean fileOk = false;
		
		String fileName = file.getOriginalFilename();
		Path path = Paths.get("src/main/resources/static/media/" + fileName);
		byte[] bytes = file.getBytes();
		
		if(!file.isEmpty()) {
			if(fileName.endsWith("png") || fileName.endsWith("jpg")) {
				fileOk = true;
			}
		}else {
			fileOk = true;
		}
		
		redirectAttribute.addFlashAttribute("message" , "Product edited");
		redirectAttribute.addFlashAttribute("alertClass" , "alert-success");
		
		String slug = product.getName().toLowerCase().replace(" ", "-");
		
		Product tempProduct = productRepository.findBySlugAndIdNot(slug , product.getId());
		
		if(!fileOk) {
			redirectAttribute.addFlashAttribute("message" , "File must be JPG or PNG");
			redirectAttribute.addFlashAttribute("alertClass" , "alert-danger");
			redirectAttribute.addFlashAttribute("product" , product);
			
		}else if(tempProduct != null) {
			redirectAttribute.addFlashAttribute("message" , "Product already exists");
			redirectAttribute.addFlashAttribute("alertClass" , "alert-danger");
			redirectAttribute.addFlashAttribute("product" , product);
		}else {
			product.setSlug(slug);
			
			if(!file.isEmpty()) {
				Path path2 = Paths.get("src/main/resources/static/media/" + currentProduct.getImage());
				Files.delete(path2);
				product.setImage(fileName);
				Files.write(path ,  bytes);
			}else {
				product.setImage(currentProduct.getImage());
			}
			productRepository.save(product);
			
		
		}
		return "redirect:/admin/products/edit/" + product.getId();
	}
	
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable int id , RedirectAttributes redirectAttributes) throws IOException {
		
		Product product = productRepository.getOne(id);
		Product currentProduct = productRepository.getOne(product.getId());
		
		Path path2 = Paths.get("src/main/resources/static/media/" + currentProduct.getImage());
		
		Files.delete(path2);
		
		productRepository.deleteById(id);
		
		redirectAttributes.addFlashAttribute("message" , "Product deleted");
		redirectAttributes.addFlashAttribute("alertClass" , "alert-success");
		
		return "redirect:/admin/products";
	}
}
