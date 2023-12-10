package com.decagon.ecommerce.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Data
public class UsersDTO {
    private String username;
    private String password;
    private String fullName;
    private String role;

}
