package com.example.study.jwt;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RefreshTokenMapper {

    RefreshToken findByEmail(@Param("email") String email);

    void save(@Param("refreshToken") RefreshToken refreshToken);

    void deleteByEmail(@Param("email") String email);

    void update(@Param("refreshToken") RefreshToken refreshToken);
}
