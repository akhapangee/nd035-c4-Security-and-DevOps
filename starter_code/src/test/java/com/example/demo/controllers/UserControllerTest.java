package com.example.demo.controllers;


import com.example.demo.exception.ApiRequestException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserController userController;

    private static User user;

    @BeforeAll
    public static void init() {
        user = getTestUser();
    }

    @BeforeEach
    public void beforeEach() {
        when(bCryptPasswordEncoder.encode(user.getPassword())).thenReturn("hashedTestPassword");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    }

    private static User getTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("password1234");
        user.setCart(new Cart());
        return user;
    }

    @DisplayName("createUser")
    @Test
    public void testCreateUser() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(user.getUsername());
        request.setPassword(user.getPassword());
        request.setConfirmPassword(user.getPassword());

        final ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(request.getUsername(), response.getBody().getUsername());
        assertEquals("hashedTestPassword", response.getBody().getPassword());

        // Check password requirement
        request.setPassword("pass");
        request.setConfirmPassword("pass");
        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> {
            userController.createUser(request);
        });
        Assertions.assertEquals("Please make sure minimum password length is 8.", exception.getMessage());
    }

    @Test
    public void testFindByUserName() {
        ResponseEntity<User> response = userController.findByUserName(user.getUsername());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getUsername(), response.getBody().getUsername());
    }

    @Test
    public void testFindById() {
        ResponseEntity<User> response = userController.findById(user.getId());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getId(), response.getBody().getId());
    }
}
