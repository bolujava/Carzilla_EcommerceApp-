package com.decagon.ecommerce.controllers;

import com.decagon.ecommerce.dtos.PasswordDTO;
import com.decagon.ecommerce.dtos.UsersDTO;
import com.decagon.ecommerce.models.Users;
import com.decagon.ecommerce.serviceImpl.UsersServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/user")
@Slf4j
public class UsersController {
    private UsersServiceImpl usersService;

    @Autowired
    public UsersController(UsersServiceImpl usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/register")
    public String indexPage(Model model){
        model.addAttribute("user", new UsersDTO());
        return "sign-up";
    }

    @GetMapping("/login")
    public ModelAndView loginPage(){
        return  new ModelAndView("login")
                .addObject("user", new UsersDTO());
    }

    @PostMapping("/sign-up")
    public String signUp(@ModelAttribute UsersDTO usersDTO){
        Users user = usersService.saveUser.apply(new Users(usersDTO));
        log.info("User details ---> {}", user);
        return "successful-register";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute UsersDTO usersDTO, HttpServletRequest request, Model model){
        Users user = usersService.findUsersByUsername.apply(usersDTO.getUsername());
        log.info("User details ---> {}", user);
        if (usersService.verifyUserPassword
                .apply(PasswordDTO.builder()
                        .password(usersDTO.getPassword())
                        .hashPassword(user.getPassword())
                        .build())){
            HttpSession session = request.getSession();
            session.setAttribute("userID", user.getId());
            return "redirect:/products/all";
        }
        return "redirect:/user/login";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "index";
    }
//    @GetMapping("/admin/register")
//    public String adminRegisterPage(Model model){
//        model.addAttribute("admin", new UsersDTO());
//        return "admin-register";
//    }
//    @PostMapping("/admin/sign-up")
//    public String adminSignUp(@ModelAttribute UsersDTO adminDTO) {
//        // Assuming you have a role field in your Users entity to distinguish between admin and regular users
//        adminDTO.setRole("ADMIN");
//
//        Users admin = usersService.saveUser.apply(new Users(adminDTO));
//        log.info("Admin details ---> {}", admin);
//        return "successful-admin-register";
//    }
//    @GetMapping("/admin/login")
//    public ModelAndView adminLoginPage() {
//        return new ModelAndView("admin-login")
//                .addObject("admin", new UsersDTO());
//    }
//    @PostMapping("/admin/login")
//    public String adminLogin(@ModelAttribute UsersDTO adminDTO, HttpServletRequest request, Model model) {
//        Users admin = usersService.findUsersByUsername.apply(adminDTO.getUsername());
//        log.info("Admin details ---> {}", admin);
//        if (admin != null && admin.getRole().equals("ADMIN") &&
//                usersService.verifyUserPassword
//                        .apply(PasswordDTO.builder()
//                                .password(adminDTO.getPassword())
//                                .hashPassword(admin.getPassword())
//                                .build())) {
//            HttpSession session = request.getSession();
//            session.setAttribute("adminID", admin.getId());
//            return "redirect:/admin/dashboard"; // Redirect to admin dashboard
//        }
//        return "redirect:/user/admin/login";
//    }
}