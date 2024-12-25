package com.yufung.demo.service.impl;

import com.yufung.demo.mapper.UserMapper;
import com.yufung.demo.model.dto.UserDTO;
import com.yufung.demo.model.entity.User;
import com.yufung.demo.model.enumeration.UserStatus;
import com.yufung.demo.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

/**
 * @author sing-fung
 * @since 1/22/2023
 */

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

//    @Autowired
//    public UserServiceImpl(UserMapper userMapper) {
//        this.userMapper = userMapper;
//    }

    public User addUser(UserDTO dto) {
        if(userMapper.findByUsername(dto.getUsername()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "username has been registered");
        }

        if(userMapper.findByEmail(dto.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email has been registered");
        }

        User user = new User(dto);

        user.setStatus(UserStatus.enabled);
        user.setCreateTime(new Date());
        user.setTs(new Date());

        userMapper.insert(user);
        return user;
    }

    public List<User> listAllUsers() {
        return userMapper.findByOrderByIdDesc();
    }

    public User getUserById(Integer id) {
        User user = userMapper.selectById(id);

        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return user;
    }

    public User updateUser(Integer id, UserDTO dto) {
        if(dto.getUsername() != null && userMapper.findByUsernameAndIdNot(dto.getUsername(), id) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "username has been registered");
        }

        if(dto.getEmail() != null && userMapper.findByEmailAndIdNot(dto.getEmail(), id) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email has been registered");
        }

        User user = getUserById(id);
        dto.setPassword(user.getPassword());
        BeanUtils.copyProperties(dto, user);
        user.setTs(new Date());
        userMapper.updateById(user);

        return user;
    }

    public User updateUserStatus(Integer id, UserStatus status) {
        User user = getUserById(id);

        if(!user.getStatus().equals(status)) {
            user.setStatus(status);
            user.setTs(new Date());
            userMapper.updateById(user);
        }

        return user;
    }
}
