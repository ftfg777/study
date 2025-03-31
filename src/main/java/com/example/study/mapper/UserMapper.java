package com.example.study.mapper;

import com.example.study.model.User;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Insert("INSERT INTO users(name, email) VALUES(#{name}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertUser(User user);

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM users")
    List<User> findAll();

    @Update("UPDATE users SET name=#{name}, email=#{email} WHERE id=#{id}")
    User updateUser(User user);

    @Delete("DELETE FROM users WHERE id=#{id}")
    void deleteUser(Long id);

    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int countByEmail(String email);
}
