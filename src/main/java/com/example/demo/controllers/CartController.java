package com.example.demo.controllers;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.models.Cart;
import com.example.demo.models.Product;
import com.example.demo.models.ProductRepository;

@Controller
@RequestMapping("/cart")
@SuppressWarnings("unchecked")
public class CartController {

	@Autowired
	private ProductRepository productRepository;

	@GetMapping("/add/{id}")
	public String add(@PathVariable int id, HttpSession session, Model theModel,
			@RequestParam(value = "cartPage", required = false) String cartPage) {

		Product product = productRepository.getOne(id);

		if (session.getAttribute("cart") == null) {
			HashMap<Integer, Cart> cart = new HashMap<Integer, Cart>();

			cart.put(id, new Cart(id, product.getName(), product.getPrice(), 1, product.getImage()));
			session.setAttribute("cart", cart);
		} else {
			HashMap<Integer, Cart> cart = (HashMap<Integer, Cart>) session.getAttribute("cart");
			if (cart.containsKey(id)) {
				int quant = cart.get(id).getQuantity();
				cart.put(id, new Cart(id, product.getName(), product.getPrice(), ++quant, product.getImage()));
			} else {
				cart.put(id, new Cart(id, product.getName(), product.getPrice(), 1, product.getImage()));
				session.setAttribute("cart", cart);
			}
		}

		HashMap<Integer, Cart> cart = (HashMap<Integer, Cart>) session.getAttribute("cart");

		int size = 0;
		double total = 0;

		for (Cart value : cart.values()) {
			size += value.getQuantity();
			total += value.getQuantity() * Double.parseDouble(value.getPrice());
		}

		theModel.addAttribute("size", size);
		theModel.addAttribute("total", total);

		if (cartPage != null) {
			return "redirect:/cart/view";
		}

		return "cart_view";
	}

	@GetMapping("/view")
	public String view(HttpSession session, Model theModel) {
		if (session.getAttribute("cart") == null) {
			return "redirect:/";
		}

		HashMap<Integer, Cart> cart = (HashMap<Integer, Cart>) session.getAttribute("cart");
		theModel.addAttribute("cart", cart);
		theModel.addAttribute("notCartViewPage", true);

		return "cart";
	}

	@GetMapping("/subtract/{id}")
	public String subtract(@PathVariable int id, HttpSession session, Model theModel,
			@RequestParam(value = "cartPage", required = false) String cartPage , HttpServletRequest httpServletRequest) {

		HashMap<Integer , Cart> cart = (HashMap<Integer , Cart>) session.getAttribute("cart");
		
		Product product = productRepository.getOne(id);
		
		int quantity = cart.get(id).getQuantity();
		
		if(quantity == 1) {
			cart.remove(id);
			if(cart.size() == 0) {
				session.removeAttribute("cart");
			}
		}else {
			cart.put(id, new Cart(id, product.getName(), product.getPrice(), --quantity, product.getImage()));
		}
		
		String refererLink = httpServletRequest.getHeader("referer");
				
		return "redirect:" + refererLink;
	}
	
	@GetMapping("/remove/{id}")
	public String remove(@PathVariable int id , Model theModel , HttpSession session, HttpServletRequest request) {
		
		HashMap<Integer , Cart> cart = (HashMap<Integer , Cart>) session.getAttribute("cart");
		
		cart.remove(id);
		if(cart.size() == 0) {
			session.removeAttribute("cart");
		}
		
		String refererLink = request.getHeader("referer");
		
		return "redirect:" + refererLink;
	}
	
	@GetMapping("/clear")
	public String clear(HttpSession session , HttpServletRequest request) {
		session.removeAttribute("cart");
		
		String refererLink = request.getHeader("referer");
		
		return "redirect:" + refererLink;
	}
}
