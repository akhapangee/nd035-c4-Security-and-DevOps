package com.example.demo.controllers;

import com.example.demo.exception.ApiRequestException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        log.info("Fetching user with id: '{}'", id);
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            log.info("User with id {} found", id);
        }
        return ResponseEntity.of(user);
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        log.info("Fetching user by username: '{}'", username);
        User user = userRepository.findByUsername(username);
        if (user != null) {
            log.info("User with username {} found", username);
        }
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        log.info("Executing method...");
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);

        Optional<User> existingUser = Optional.ofNullable(userRepository.findByUsername(user.getUsername()));
        if (existingUser.isPresent()) {
            log.error("[User Creation Failure]: Username '{}' already exists.", createUserRequest.getUsername());
            throw new ApiRequestException(String.format("[User Creation Failure]: Username '%s' already exists.", createUserRequest.getUsername()));
        }

        if (createUserRequest.getPassword() == null || createUserRequest.getConfirmPassword() == null) {
            log.error("[User Creation Failure]: For username '{}' password or confirm password can not be empty", createUserRequest.getUsername());
            throw new ApiRequestException(String.format("[User Creation Failure]: For username '%s' password or confirm password can not be empty", createUserRequest.getUsername()));
        }

        if (createUserRequest.getPassword().length() < 8) {
            log.error("[User Creation Failure]: For username '{}' please make sure minimum password length is 8.", createUserRequest.getUsername());
            throw new ApiRequestException(String.format("[User Creation Failure]: For username '%s' please make sure minimum password length is 8.", createUserRequest.getUsername()));
        }
        if (!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            log.error("[User Creation Failure]: For username '{}' password and confirmPassword are not same.", createUserRequest.getUsername());
            throw new ApiRequestException(String.format("[User Creation Failure]: For username '%s' password and confirmPassword are not same.", createUserRequest.getUsername()));
        }
        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        userRepository.save(user);
        log.info("[User Creation Success]: User '{}' created successfully!", user.getUsername());
        return ResponseEntity.ok(user);
    }

}
