package com.example.study.service;

import com.example.study.exception.ErrorCode;
import com.example.study.mapper.UserMapper;
import com.example.study.model.User;
import com.example.study.util.ExceptionUtils;
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
    public void createUser(User user) {
        checkEmail(user.getEmail());

        userMapper.createdUser(user);
    }

    public User getUserById(Long id) {
        User user = userMapper.getById(id);
        ExceptionUtils.throwIfNotFound(user, ErrorCode.USER_NOT_FOUND);

        return user;
    }

    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    @Transactional
    public void updateUser(Long id, User user) {
        User updatedUser = getUserById(id);
        ExceptionUtils.throwIfNotFound(updatedUser, ErrorCode.USER_NOT_FOUND);

        user.setId(id);
        userMapper.updateUser(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userMapper.getById(id);
        ExceptionUtils.throwIfNotFound(user, ErrorCode.USER_NOT_FOUND);

        userMapper.deleteUser(id);
    }

    public void checkEmail(String email) {
        int isDuplicate = userMapper.countByEmail(email);
        ExceptionUtils.throwIfExists(isDuplicate, ErrorCode.EMAIL_ALREADY_EXISTS);

        userMapper.findByEmail(email);
    }

}
