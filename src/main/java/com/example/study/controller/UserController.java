package com.example.study.controller;

import com.example.study.model.User;
import com.example.study.service.UserService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Integer> createUser(@RequestBody User user) {
        int result = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User userDto = userService.getUserById(id);
        if (userDto == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(userDto);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> usersDto = userService.getAllUsers();
        return ResponseEntity.ok(usersDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        if (userService.getUserById(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        User updatedUserDto = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUserDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.getUserById(id) == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


}
