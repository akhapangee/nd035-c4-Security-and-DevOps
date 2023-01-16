package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        user = getTestUser();
    }

    @BeforeEach
    public void beforeEach() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
    }

    private static User getTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("password1234");

        Cart cart = new Cart();
        BigDecimal total = new BigDecimal("00.00");
        cart.setId(1L);
        cart.setItems(Lists.newArrayList(getTestItem()));
        cart.setUser(user);
        cart.setTotal(total.add(getTestItem().getPrice()));

        user.setCart(cart);

        return user;
    }

    private static Item getTestItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(new BigDecimal("10.10"));
        item.setDescription("Test Item Desc");
        return item;
    }

    @Test
    public void submitOrder() {
        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody().getUser());
        assertEquals(user.getCart().getItems(), response.getBody().getItems());
    }

    @Test
    public void getOrdersForUser() {
        UserOrder userOrder = new UserOrder();
        userOrder.setUser(user);
        userOrder.setItems(user.getCart().getItems());
        userOrder.setTotal(user.getCart().getTotal());

        when(orderRepository.findByUser(user)).thenReturn(Lists.newArrayList(userOrder));

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody().get(0).getUser());
        assertEquals(user.getCart().getItems(), response.getBody().get(0).getItems());
    }
}
