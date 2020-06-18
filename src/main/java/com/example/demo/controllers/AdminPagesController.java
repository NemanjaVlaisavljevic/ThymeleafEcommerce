package com.example.demo.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.models.Page;
import com.example.demo.models.PageRepository;

@Controller
@RequestMapping("/admin/pages")
public class AdminPagesController {

	@Autowired
	private PageRepository pageRepository;
	
	@GetMapping
	public String index(Model theModel) {
		
		List<Page> pages = pageRepository.findAllByOrderBySortingAsc();
		theModel.addAttribute("pages" , pages);
		
		return "admin/pages/index";
	}
	
	@GetMapping("/add")
	public String add(@ModelAttribute Page page) {
	// Moze sa modelAttribute ili kako je na liniji ispod
	//	theModel.addAttribute("page" , new Page());
		
		return "admin/pages/add";
	}
	
	@PostMapping("/add")
	public String add(@Valid Page page , BindingResult theBindingResult, RedirectAttributes redirectAttributes , Model theModel) {
		
		if(theBindingResult.hasErrors()) {
			return "admin/pages/add";
		}
		
		redirectAttributes.addFlashAttribute("message" , "Page added");
		redirectAttributes.addFlashAttribute("alertClass" , "alert-success");
		
		String slug = page.getSlug() == "" ? page.getTitle().toLowerCase().replace(" ", "-") : page.getSlug().toLowerCase().replace(" ", "-");
		
		Page slugExists = pageRepository.findBySlug(slug);
		
		if(slugExists != null) {
			redirectAttributes.addFlashAttribute("message" , "Slug exists , choose another");
			redirectAttributes.addFlashAttribute("alertClass" , "alert-danger");
		}else {
			page.setSlug(slug);
			page.setSorting(100);
			
			pageRepository.save(page);
		}
		
		return "redirect:/admin/pages/add";
	}
	
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable int id, Model theModel) {
		Page page = pageRepository.getOne(id);
		theModel.addAttribute("page" , page);
		
		return "admin/pages/edit";
	}
	
	@PostMapping("/edit")
	public String edit(@Valid Page page , BindingResult theBindingResult, RedirectAttributes redirectAttributes , Model theModel) {
		
		Page pageCurrent = pageRepository.getOne(page.getId());
		
		if(theBindingResult.hasErrors()) {
			theModel.addAttribute("pageTitle" , pageCurrent.getTitle());
			return "admin/pages/edit";
		}
		
		redirectAttributes.addFlashAttribute("message" , "Page edited");
		redirectAttributes.addFlashAttribute("alertClass" , "alert-success");
		
		String slug = page.getSlug() == "" ? page.getTitle().toLowerCase().replace(" ", "-") : page.getSlug().toLowerCase().replace(" ", "-");
		
		Page slugExists = pageRepository.findBySlugAndIdNot(slug , page.getId());
		
		if(slugExists != null) {
			redirectAttributes.addFlashAttribute("message" , "Slug exists , choose another");
			redirectAttributes.addFlashAttribute("alertClass" , "alert-danger");
		}else {
			page.setSlug(slug);
			
			pageRepository.save(page);
		}
		
		return "redirect:/admin/pages/edit/" + page.getId();
	}
	
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
		pageRepository.deleteById(id);
		
		redirectAttributes.addFlashAttribute("message" , "Page deleted");
		redirectAttributes.addFlashAttribute("alertClass" , "alert-success");
		
		return "redirect:/admin/pages";
	}
	
	@PostMapping("/reorder")
	public @ResponseBody String reorder(@RequestParam("id[]") int[] id) {
		
		int count = 1;
		
		Page page;
		
		for(int pageId : id) {
			page = pageRepository.getOne(pageId);
			page.setSorting(count);
			pageRepository.save(page);
			count++;
		}
		
		return "ok";
	}

}
