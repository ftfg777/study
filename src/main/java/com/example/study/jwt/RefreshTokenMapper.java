package com.example.study.jwt;

import java.sql.Timestamp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RefreshTokenMapper {

    RefreshToken findByEmail(@Param("email") String email);

    void save(@Param("email") String email, @Param("token") String token, @Param("expiryAt") Timestamp expiryAt);

    void deleteByEmail(@Param("email") String email);
}
