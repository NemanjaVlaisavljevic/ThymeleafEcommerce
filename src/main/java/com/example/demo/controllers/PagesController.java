package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.models.Page;
import com.example.demo.models.PageRepository;

@Controller
@RequestMapping("/")
public class PagesController {

	@Autowired
	private PageRepository pageRepository;
	
	@GetMapping()
	public String home(Model theModel) {
		Page page = pageRepository.findBySlug("home");
		
		theModel.addAttribute("page" , page);
		
		return "page";
	}
	
	@GetMapping("/{slug}")
	public String home(@PathVariable String slug , Model theModel) {
		Page page = pageRepository.findBySlug(slug);
		
		if(page == null) {
			return "redirect:/";
		}
		
		theModel.addAttribute("page" , page);
		
		return "page";
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
}
