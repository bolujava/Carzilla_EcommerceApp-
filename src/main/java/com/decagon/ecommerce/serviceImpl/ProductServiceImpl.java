package com.decagon.ecommerce.serviceImpl;


import com.decagon.ecommerce.models.Cart;
import com.decagon.ecommerce.models.Order;
import com.decagon.ecommerce.models.Product;
import com.decagon.ecommerce.models.Users;
import com.decagon.ecommerce.repositories.ProductRepositories;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class ProductServiceImpl {

    private ProductRepositories productRepositories;

    @Autowired
    public ProductServiceImpl(ProductRepositories productRepositories) {
        this.productRepositories = productRepositories;
    }

    public Supplier<List<Product>> findAllProducts = ()->productRepositories.findAll();
    public Function<Long, Product> findById = (id)->
            productRepositories.findById(id)
                    .orElseThrow(()->
                            new NullPointerException("No such product found with ID: "+ id));


    public void addProductToCart(Long id, HttpServletRequest request) {
        HttpSession session = request.getSession();
     Cart cart;
        if (session.getAttribute("cart")!=null){
            cart = (Cart) session.getAttribute("cart");
            cart.setProductIds(cart.getProductIds()+","+ id);
            session.setAttribute("cartItems", cart.getProductIds().split(",").length);
        }
        else {
            cart = Cart.builder().productIds(id.toString())
                    .userId((Long) session.getAttribute("userID")).build();
            session.setAttribute("cart", cart);
            session.setAttribute("cartItems", cart.getProductIds().split(",").length);
        }
    }
    public void removeProductFromCart(Long id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart != null) {
            String[] productIds = cart.getProductIds().split(",");

            // Remove the product ID from the list
            List<String> updatedProductIds = new ArrayList<>(Arrays.asList(productIds));
            updatedProductIds.remove(id.toString());

            // Update the cart and cartItems in the session
            cart.setProductIds(String.join(",", updatedProductIds));
            session.setAttribute("cartItems", updatedProductIds.size());
        }
    }
    public void checkOutCart(HttpSession session, Model model) {
        Cart cart = (Cart) session.getAttribute("cart");
        List<Product> productList = new ArrayList<>();
        List<String> productIds = Arrays.stream(cart.getProductIds().split(",")).toList();
        productIds.forEach(id->{
            productList.add(productRepositories.findById(Long.parseLong(id)).orElseThrow(()->
                    new NullPointerException("No such product found with ID: "+ id)));
        });

        final BigDecimal[] totalPrice = {new BigDecimal(0)};
        productList.forEach(product -> totalPrice[0] = totalPrice[0].add(product.getPrice()));
        model.addAttribute("totalPrice", "Total Price: $"+ totalPrice[0]);
        session.setAttribute("cart", null);
        Order order = Order.builder()
                .productList(productList)
                .userId((Long) session.getAttribute("userID"))
                .totalPrice(totalPrice[0])
                .build();
        session.setAttribute("order", order);
        model.addAttribute("order", order);
    }
    public Function<Product, Product>editProduct = (product) -> productRepositories.save(product);
    public Consumer<Long> removeProduct = (productId) -> productRepositories.deleteById(productId);
    public Function<Product, Product> addProduct = (product)->productRepositories.save(product);

}
