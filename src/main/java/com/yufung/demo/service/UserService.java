package com.yufung.demo.service;

import com.yufung.demo.model.dto.UserDTO;
import com.yufung.demo.model.entity.User;
import com.yufung.demo.model.enumeration.UserStatus;

import java.util.List;

public interface UserService {
    User addUser(UserDTO dto);

    List<User> listAllUsers();

    User getUserById(Integer id);

    User updateUser(Integer id, UserDTO dto);

    User updateUserStatus(Integer id, UserStatus status);

    List<User> getUsersByIds(List<Integer> ids);
}
