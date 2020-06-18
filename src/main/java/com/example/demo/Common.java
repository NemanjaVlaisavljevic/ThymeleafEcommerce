package com.example.demo;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.models.Cart;
import com.example.demo.models.Category;
import com.example.demo.models.CategoryRepository;
import com.example.demo.models.Page;
import com.example.demo.models.PageRepository;

@ControllerAdvice
@SuppressWarnings("unchecked")
public class Common {

	@Autowired
	private PageRepository pageRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@ModelAttribute
	public void sharedData(Model theModel , HttpSession session , Principal principal) {
		
		if(principal != null) {
			theModel.addAttribute("principal" , principal.getName());
		}
		
		List<Page> pages = pageRepository.findAllByOrderBySortingAsc();
		
		List<Category> categories = categoryRepository.findAll();
		
		boolean cartActive = false;
		
		if(session.getAttribute("cart") != null) {
			HashMap<Integer, Cart> cart = (HashMap<Integer , Cart>) session.getAttribute("cart");
			int size = 0;
			double total = 0;
			
			for(Cart value : cart.values()) {
				size += value.getQuantity();
				total += value.getQuantity() * Double.parseDouble(value.getPrice());
			}
			
			theModel.addAttribute("csize" , size);
			theModel.addAttribute("ctotal" , total);
			
			cartActive = true;
		}
		
		theModel.addAttribute("categories" , categories);
		
		theModel.addAttribute("cpages" , pages);
		theModel.addAttribute("cartActive" , cartActive);
	}
	
	
}
