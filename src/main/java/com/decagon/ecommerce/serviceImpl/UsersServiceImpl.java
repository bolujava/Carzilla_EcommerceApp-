package com.decagon.ecommerce.serviceImpl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.decagon.ecommerce.dtos.PasswordDTO;
import com.decagon.ecommerce.models.Users;
import com.decagon.ecommerce.repositories.UsersRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UsersServiceImpl {

    private UsersRepositories usersRepositories;

    @Autowired
    public UsersServiceImpl(UsersRepositories usersRepositories) {
        this.usersRepositories = usersRepositories;
    }

    public Function<String, Users> findUsersByUsername = (username)->
            usersRepositories.findByUsername(username)
                    .orElseThrow(()->new NullPointerException("User not found!"));
    public Function<Long, Users> findUsersById = (id)->
            usersRepositories.findById(id)
                    .orElseThrow(()->new NullPointerException("User not found!"));

    public Function<Users, Users> saveUser = (user)->usersRepositories.save(user);

    public Function<PasswordDTO, Boolean> verifyUserPassword = passwordDTO -> BCrypt.verifyer()
            .verify(passwordDTO.getPassword().toCharArray(),
                    passwordDTO.getHashPassword().toCharArray())
            .verified;
    public Function<String, Users> findAdminByUsername = (username) ->
            usersRepositories.findByUsernameAndRole(username, "ADMIN")
                    .orElseThrow(() -> new NullPointerException("Admin not found!"));

    public Function<PasswordDTO, Boolean> verifyAdminPassword = passwordDTO -> {
        return BCrypt.verifyer()
                .verify(passwordDTO.getPassword().toCharArray(),
                        passwordDTO.getHashPassword().toCharArray())
                .verified;
    };
}
