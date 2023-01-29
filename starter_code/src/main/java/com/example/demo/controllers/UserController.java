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
    public static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        log.info("Finding user by ID: {}", id);
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        log.info("Finding user by username: {}", username);
        User user = userRepository.findByUsername(username);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);

        Optional<User> existingUser = Optional.ofNullable(userRepository.findByUsername(user.getUsername()));
        if (existingUser.isPresent()) {
            log.error("Username '{}' already exists.", user.getUsername());
            throw new ApiRequestException(String.format("Username '%s' already exists.", user.getUsername()));
        }

        if (createUserRequest.getPassword() == null || createUserRequest.getConfirmPassword() == null) {
            log.error("Password or confirm password can not be empty");
            throw new ApiRequestException("Password or confirm password can not be empty");
        }

        if (createUserRequest.getPassword().length() < 8) {
            log.error("Please make sure minimum password length is 8.");
            throw new ApiRequestException("Please make sure minimum password length is 8.");
        }
        if (!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            log.error("Password and confirmPassword are not same.");
            throw new ApiRequestException("Password and confirmPassword are not same.");
        }
        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        userRepository.save(user);
        log.info("User '{}' created successfully!", user.getUsername());
        return ResponseEntity.ok(user);
    }

}
