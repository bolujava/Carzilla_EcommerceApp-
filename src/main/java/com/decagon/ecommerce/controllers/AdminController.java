package com.decagon.ecommerce.controllers;

import com.decagon.ecommerce.dtos.PasswordDTO;
import com.decagon.ecommerce.dtos.UsersDTO;
import com.decagon.ecommerce.models.Product;
import com.decagon.ecommerce.models.Users;
import com.decagon.ecommerce.serviceImpl.ProductServiceImpl;
import com.decagon.ecommerce.serviceImpl.UsersServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    private UsersServiceImpl usersService;
    private ProductServiceImpl productService;
    @Autowired
    public AdminController(UsersServiceImpl usersService, ProductServiceImpl productService) {
        this.usersService = usersService;
        this.productService = productService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, HttpSession session) {
        // Check if the user making the request is an admin
        // You may use Spring Security or custom logic for this check
        Long adminID = (Long) session.getAttribute("adminID");
        if (adminID == null) {
            // Redirect to the admin login page if not logged in
            return "redirect:/admin/login";
        }

        // Retrieve admin details or any other admin-specific data
        Users adminUser = usersService.findUsersById.apply(adminID);
        List<Product>productList = productService.findAllProducts.get();

        // Add adminUser to the model to display on the admin dashboard
        model.addAttribute("adminUser", adminUser);
        model.addAttribute("products",productList);

        // Add additional attributes or logic based on your admin dashboard requirements

        return "admin-dashboard";
    }
    @GetMapping("/edit/{productId}")
    public String editProductForm(@PathVariable Long productId, Model model) {
        Product product = productService.findById.apply(productId);
        model.addAttribute("product", product);
        return "edit-product-form";
    }

    @PostMapping("/edit-product")
    public String editProduct(@ModelAttribute Product product){
           productService.editProduct.apply(product);
           return "redirect:/admin/dashboard";
        }
    @GetMapping("/delete-product")
    public String deleteProduct(@RequestParam Long productId) {
        productService.removeProduct.accept(productId);
        return "redirect:/admin/dashboard";
    }

    // Add more admin-related endpoints and methods as needed
    @GetMapping("/register")
    public String adminRegisterPage(Model model){
        model.addAttribute("admin", new UsersDTO());
        return "admin-register";
    }
    @PostMapping("/register")
    public String adminSignUp(@ModelAttribute UsersDTO adminDTO) {
        // Assuming you have a role field in your Users entity to distinguish between admin and regular users
        adminDTO.setRole("ADMIN");

        Users admin = usersService.saveUser.apply(new Users(adminDTO));
        log.info("Admin details ---> {}", admin);
        return "successful-admin-register";
    }
    @GetMapping("/login")
    public ModelAndView adminLoginPage() {
        return new ModelAndView("admin-login")
                .addObject("admin", new UsersDTO());
    }
    @PostMapping("/login")
    public String adminLogin(@ModelAttribute UsersDTO adminDTO, HttpServletRequest request, Model model) {
        Users admin = usersService.findUsersByUsername.apply(adminDTO.getUsername());
        log.info("Admin details ---> {}", admin);
        if (admin != null && admin.getRole().equals("ADMIN") &&
                usersService.verifyUserPassword
                        .apply(PasswordDTO.builder()
                                .password(adminDTO.getPassword())
                                .hashPassword(admin.getPassword())
                                .build())) {
            HttpSession session = request.getSession();
            session.setAttribute("adminID", admin.getId());
            return "redirect:/admin/dashboard"; // Redirect to admin dashboard
        }
        return "redirect:/admin/login";
    }
    @GetMapping("/add-product")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product()); // Assuming you want to bind the form to a new Product object
        return "add-product-form"; // Assuming you have an HTML template for adding products
    }
    @PostMapping("/add-product")
    public String addProduct(@ModelAttribute Product product) {
        productService.addProduct.apply(product);
        return "redirect:/admin/dashboard";
    }

}
