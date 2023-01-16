package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.Assertions;
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
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartControllerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private CartController cartController;

    private static User user;
    private static Item item;

    @BeforeAll
    public static void beforeAll() {
        user = getTestUser();
        item = getTestItem();
    }

    @BeforeEach
    public void beforeEach() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
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
        user.setCart(new Cart());
        return user;
    }

    @Test
    public void testAddTocart() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(1L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.addTocart(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(item.getId(), response.getBody().getItems().get(0).getId());

    }

    @Test
    public void testRemoveFromcart() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(item.getId());
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(0, response.getBody().getItems().size());
    }

}
