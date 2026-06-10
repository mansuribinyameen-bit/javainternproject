package com.example.onlinebookstore.user;

import com.example.onlinebookstore.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Set;

@Controller
public class AuthController {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.default-role:CUSTOMER}")
    private String defaultRole;

    public AuthController(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("request", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("request") RegisterRequest request,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        if (customerRepository.existsByEmail(request.getEmail())) {
            bindingResult.rejectValue("email", "email.exists", "Email already registered");
            return "register";
        }

        Customer customer = new Customer();
        customer.setEmail(request.getEmail());
        customer.setFullName(request.getFullName());
        customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        customer.setRoles(Set.of("ROLE_" + defaultRole.toUpperCase()));

        customerRepository.save(customer);

        redirectAttributes.addFlashAttribute("message", "Registration successful. Please login.");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                         @RequestParam(value = "logout", required = false) String logout,
                         Model model) {
        if (error != null) model.addAttribute("error", "Invalid username or password");
        if (logout != null) model.addAttribute("message", "Logged out successfully");
        return "login";
    }

    @GetMapping("/me")
    public String me(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        return "me";
    }
}

