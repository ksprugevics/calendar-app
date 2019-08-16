package com.example.calendar.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.calendar.models.User;
import com.example.calendar.service.SecurityService;
import com.example.calendar.service.UserService;
import com.example.calendar.validator.UserValidator;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;
    

    @GetMapping("/registration")
    public String registration(Model model) {
    	
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = "";
		if (principal instanceof UserDetails) {
		  username = ((UserDetails)principal).getUsername();
		} else {
		  username = principal.toString();
		}
		
		//Doesn't allow authorised users to register again.
		if(username != "anonymousUser")
			return "redirect:/calendar";
    	
    	
        model.addAttribute("userForm", new User());
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {       	
        	return "registration";
        }
        

        userService.save(userForm);

        securityService.autoLogin(userForm.getUsername(), userForm.getPasswordConfirm());
        return "redirect:/add-new-calendar";
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
    	
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = "";
		if (principal instanceof UserDetails) {
		  username = ((UserDetails)principal).getUsername();
		} else {
		  username = principal.toString();
		}
		
		//Doesn't let authorised users log in again
		if(username != "anonymousUser")
			return "redirect:/calendar";
    	
        if (error != null)
            model.addAttribute("error", "Your username or password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }///login POST controller is provided by Spring Security

}