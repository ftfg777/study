package com.example.study.service;

import com.example.study.exception.CommonExceptionHandler;
import com.example.study.exception.ErrorCode;
import com.example.study.mapper.UserMapper;
import com.example.study.model.User;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Transactional
    public int createUser(User user) {
        int result = userMapper.countByEmail(user.getEmail());
        //이메일 중복 체크
        if (result > 0) {
            throw new CommonExceptionHandler(ErrorCode.USER_ALREADY_EXISTS);
        }

       return userMapper.insertUser(user);
    }

    public User getUserById(Long id) {
        User user = userMapper.getById(id);
        if (user == null){
            throw new CommonExceptionHandler(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    public User updateUser(Long id, User user) {
        user.setId(id);
        return userMapper.updateUser(user);
    }

    public void deleteUser(Long id) {
        userMapper.deleteUser(id);
    }

    public boolean findByEmail(String email) {
        return userMapper.findByEmail(email);
    }
}
