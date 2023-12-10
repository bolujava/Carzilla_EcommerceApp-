package com.decagon.ecommerce.controllers;


import com.decagon.ecommerce.models.Product;
import com.decagon.ecommerce.serviceImpl.OrderServiceImpl;
import com.decagon.ecommerce.serviceImpl.ProductServiceImpl;
import com.decagon.ecommerce.serviceImpl.UsersServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductServiceImpl productService;
    private UsersServiceImpl usersService;
    private final OrderServiceImpl orderService;
    @Autowired
    public ProductController(ProductServiceImpl productService, UsersServiceImpl usersService, OrderServiceImpl orderService) {
        this.productService = productService;
        this.usersService = usersService;
        this.orderService = orderService;
    }
    @GetMapping("/all")
    public ModelAndView findAllProducts(HttpServletRequest request){
        HttpSession session = request.getSession();
        List<Product> productList = productService.findAllProducts.get();
        return new ModelAndView("dashboard")
                .addObject("products", productList)
                .addObject("cartItems", "Cart Items: "+session.getAttribute("cartItems"));
    }
    @GetMapping("/add-cart")
    public String addToCart(@RequestParam(name = "cart") Long id, HttpServletRequest request, Model model){
        productService.addProductToCart(id, request);
        return "redirect:/products/all";
    }
    @GetMapping("/remove-cart")
    public String removeFromCart(@RequestParam(name = "cart") Long id, HttpServletRequest request, Model model) {
        productService.removeProductFromCart(id, request);
        return "redirect:/products/all";
    }
    @GetMapping("/payment")
    public String checkOut(HttpSession session, Model model){
        productService.checkOutCart(session, model);
        model.addAttribute("paid", "");
        return "checkout";
    }

    @GetMapping("/pay")
    public String orderPayment(HttpSession session, Model model){

        return orderService.makePayment(session, model);
    }
//    @GetMapping("/edit/{productId}")
//    public ModelAndView editProductForm(@PathVariable Long productId, Model model) {
//        Product product = productService.findById.apply(productId);
//        return  new ModelAndView("edit-product-form")
//                .addObject("products", product);
//       // model.addAttribute("product", product);
//
//       // return "edit-product-form";  // Assuming you have an HTML template for editing products
//    }
//
//    // Handle the form submission for editing a product
//    @PostMapping("/admin/edit-product")
//    public String editProduct(@ModelAttribute Product product) {
//        productService.editProduct.apply(product);
//        return "redirect:/admin/dashboard";  // Redirect to the admin dashboard after editing
//    }
//    @PostMapping("/delete-product")
//    public String deleteProduct(@RequestParam Long productId) {
//        productService.removeProduct.accept(productId);
//        return "redirect:/admin/dashboard";  // Redirect to the admin dashboard after deletion
//    }




}
