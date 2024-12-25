package com.yufung.demo.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO
{
    @NotBlank(message = "username cannot be empty", groups = {Insert.class, Update.class})
    String username;

    @NotBlank(message = "password cannot be empty", groups = {Insert.class})
    String password;

    @Email(message = "invalid email format", groups = {Insert.class, Update.class})
    @NotBlank(message = "email cannot be empty", groups = {Insert.class, Update.class})
    String email;

    public interface Update {}

    public interface Insert {}
}