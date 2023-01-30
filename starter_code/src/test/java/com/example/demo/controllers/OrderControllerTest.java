package com.example.demo.controllers;

import com.example.demo.SampleData;
import com.example.demo.exception.ApiRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderController orderController;
    private static User user;

    @BeforeAll
    public static void init() {
        user = SampleData.getSampleUser();
    }

    @Test
    public void submitOrder() {
        // Everything is ready to submit the request i.e. there are items in the cart for user
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody().getUser());
        assertEquals(user.getCart().getItems(), response.getBody().getItems());
    }

    @Test
    public void submitOrder_with_no_items_in_cart() {

        // There are no items in the cart but client request tries to submit order from rest end point
        User mockUser = SampleData.getSampleUser();
        // Empty items for testing
        mockUser.getCart().setItems(Collections.emptyList());

        when(userRepository.findByUsername(anyString())).thenReturn(mockUser);

        Assertions.assertThrows(NotFoundException.class, () -> {
            orderController.submit(user.getUsername());
        });
    }

    @Test
    public void submitOrder_with_invalid_username() {
        // When the client request contains invalid username to submit the order
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        Assertions.assertThrows(ApiRequestException.class, () -> {
            orderController.submit(user.getUsername());
        });
    }


    @Test
    public void getOrdersForUser() {
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        UserOrder userOrder = new UserOrder();
        userOrder.setUser(user);
        userOrder.setItems(user.getCart().getItems());
        userOrder.setTotal(user.getCart().getTotal());

        when(orderRepository.findByUser(any())).thenReturn(Collections.singletonList(userOrder));

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody().get(0).getUser());
        assertEquals(user.getCart().getItems(), response.getBody().get(0).getItems());
    }

    @Test
    public void getOrdersForUser_if_user_is_null() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        Assertions.assertThrows(ApiRequestException.class, () -> {
            orderController.getOrdersForUser(user.getUsername());
        });
    }
}
