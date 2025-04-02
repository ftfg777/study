package com.example.study.jwt;

import com.example.study.exception.CommonExceptionHandler;
import com.example.study.exception.ErrorCode;
import com.example.study.mapper.UserMapper;
import com.example.study.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    public CustomUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userMapper.emailLogin(email);
        if (user == null){
            throw new CommonExceptionHandler(ErrorCode.USER_NOT_FOUND);
        }
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail()) // 이메일을 아이디로 사용
            .password(user.getPassword()) // 암호화된 비밀번호
            .roles(user.getRole()) // 역할 설정
            .build();
    }
}
