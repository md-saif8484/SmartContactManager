package com.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.Users;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@RequestMapping("/")
	public String home(Model m)
	{
		m.addAttribute("title","Home Page");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model m)
	{
		m.addAttribute("title","About Page");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model m)
	{
		m.addAttribute("title","Signup Page");
		m.addAttribute("user",new Users());
		return "signup";
	}
	
	@RequestMapping(value="/do_register", method=RequestMethod.POST)
	public String registerUser(
	    @Valid @ModelAttribute("user") Users user,
	    BindingResult result1,
	    @RequestParam(value="agreement", defaultValue = "false") boolean agreement, 
	    Model model
	) {
	    try {
	        // Check agreement
	        if (!agreement) {
	            throw new Exception("You must agree to the terms and conditions");
	        }

	        // Validation errors
	        if (result1.hasErrors()) {
	            model.addAttribute("user", user);
	            return "signup";
	        }

	        // Check if user already exists
	        if (userRepository.existsByEmail(user.getEmail())) {
	            model.addAttribute("message", new Message("User already exists!", "alert-danger"));
	            return "signup";
	        }

	        // Set default user attributes
	        user.setRole("Role_User");
	        user.setEnabled("true");
	        user.setImageUrl("default.png");
	        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

	        // Save user to DB
	        userRepository.save(user);
	        model.addAttribute("user", new Users());
	        model.addAttribute("message", new Message("Successfully registered!", "alert-success"));

	        return "signup";
	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("user", user);
	        model.addAttribute("message", new Message("Error: " + e.getMessage(), "alert-danger"));
	        return "signup";
	    }
	}
	
	@GetMapping("/signin")
	public String signin(Model m)
	{
		
		return "login";
	}

}
