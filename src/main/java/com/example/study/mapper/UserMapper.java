package com.example.study.mapper;

import com.example.study.model.User;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    User getById(@Param("id") Long id);
    void createdUser(User user);
    void deleteUserById(@Param("id") Long id);

    @Select("select * from users where email = #{email}")
    User emailLogin(String email);


    @Insert("INSERT INTO users(email, name, password, role) VALUES(#{email}, #{name}, #{password}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertUser(User user);

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM users")
    List<User> findAll();

    @Update("UPDATE users SET name=#{name}, email=#{email} WHERE id=#{id}")
    void updateUser(User user);

    @Delete("DELETE FROM users WHERE id=#{id}")
    void deleteUser(Long id);

    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int countByEmail(String email);

    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    boolean findByEmail(String email);
}
