package com.example.demo.controllers;

import com.example.demo.exception.ApiRequestException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ControllersTest {
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CartController cartController;
    @InjectMocks
    private ItemController itemController;
    @InjectMocks
    private OrderController orderController;
    @InjectMocks
    private UserController userController;
    private static User user;
    private static Item item;

    @BeforeAll
    public static void beforeAll() {
        user = getTestUser();
        item = getTestItem();
    }

    private static Item getTestItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(new BigDecimal("10.10"));
        item.setDescription("Test Item Desc");
        return item;
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

    @DisplayName("CartController.addTocart")
    @Test
    public void testAddTocart() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(1L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.addTocart(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(item.getId(), response.getBody().getItems().get(0).getId());

    }

    @DisplayName("CartController.removeFromcart")
    @Test
    public void testRemoveFromcart() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(item.getId());
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(0, response.getBody().getItems().size());
    }


    @DisplayName("ItemController.getItemById")
    @Test
    public void getItemById() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        ResponseEntity<Item> response = itemController.getItemById(item.getId());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(item.getId(), response.getBody().getId());
    }

    @DisplayName("ItemController.getItemsByName")
    @Test
    public void getItemsByName() {
        when(itemRepository.findByName(item.getName())).thenReturn(Lists.newArrayList(item));
        ResponseEntity<List<Item>> response = itemController.getItemsByName(item.getName());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(Lists.newArrayList(item), response.getBody());
    }

    @DisplayName("OrderController.submitOrder")
    @Test
    public void submitOrder() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody().getUser());
        assertEquals(user.getCart().getItems(), response.getBody().getItems());
    }

    @DisplayName("OrderController.getOrdersForUser")
    @Test
    public void getOrdersForUser() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
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

    @DisplayName("UserController.createUser")
    @Test
    public void testCreateUser() {
        when(bCryptPasswordEncoder.encode(user.getPassword())).thenReturn("hashedTestPassword");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(user.getUsername());
        request.setPassword(user.getPassword());
        request.setConfirmPassword(user.getPassword());

        final ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(request.getUsername(), response.getBody().getUsername());
        assertEquals("hashedTestPassword", response.getBody().getPassword());

        // Check password and confirm password are not same
        request.setPassword("pass");
        request.setConfirmPassword("passnotsame");
        assertThrows(ApiRequestException.class, () -> {
            userController.createUser(request);
        });

        // Check password requirement
        request.setPassword("pass");
        request.setConfirmPassword("pass");
        assertThrows(ApiRequestException.class, () -> {
            userController.createUser(request);
        });

    }

    @DisplayName("UserController.findByUserName")
    @Test
    public void testFindByUserName() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        ResponseEntity<User> response = userController.findByUserName(user.getUsername());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getUsername(), response.getBody().getUsername());
    }

    @DisplayName("UserController.findById")
    @Test
    public void testFindById() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        ResponseEntity<User> response = userController.findById(user.getId());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getId(), response.getBody().getId());
    }

}
