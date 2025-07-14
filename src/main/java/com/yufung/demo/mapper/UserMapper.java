package com.yufung.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yufung.demo.common.MybatisRedisCache;
import com.yufung.demo.model.entity.User;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@CacheNamespace(implementation=MybatisRedisCache.class, eviction=MybatisRedisCache.class)
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from demo_user where username = #{username} limit 1")
    User findByUsername(@Param("username") String username);

    @Select("select * from demo_user where email = #{email} limit 1")
    User findByEmail(@Param("email") String email);

    @Select("select * from demo_user order by id desc")
    List<User> findByOrderByIdDesc();

    @Select("select * from demo_user where username = #{username} and id != #{id} limit 1")
    User findByUsernameAndIdNot(@Param("username") String username, @Param("id") Integer id);

    @Select("select * from demo_user where email = #{email} and id != #{id} limit 1")
    User findByEmailAndIdNot(@Param("email") String email, @Param("id") Integer id);

    List<User> findByIdList(List<Integer> idList);
}