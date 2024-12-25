package com.yufung.demo.controller;

import com.yufung.demo.model.dto.UserDTO;
import com.yufung.demo.model.entity.User;
import com.yufung.demo.model.enumeration.UserStatus;
import com.yufung.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User addUser(@RequestBody @Validated(UserDTO.Insert.class) UserDTO dto) {
        return userService.addUser(dto);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.listAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Integer id, @RequestBody @Validated(UserDTO.Update.class) UserDTO dto) {
        return userService.updateUser(id, dto);
    }

    @PutMapping("/{id}/status/{status}")
    public User updateUserStatus(@PathVariable Integer id, @PathVariable UserStatus status) {
        return userService.updateUserStatus(id, status);
    }
}
