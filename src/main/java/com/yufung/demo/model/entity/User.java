package com.yufung.demo.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yufung.demo.model.dto.UserDTO;
import com.yufung.demo.model.enumeration.UserStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@TableName("demo_user")
@Data
@NoArgsConstructor
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = -8986596830247946596L;

    @TableId(type= IdType.AUTO)
    Integer id;

    String username;
    String password;
    String email;

    @Enumerated(EnumType.STRING)
    UserStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date ts;

    public User(UserDTO dto) {
        BeanUtils.copyProperties(dto, this);
    }
}