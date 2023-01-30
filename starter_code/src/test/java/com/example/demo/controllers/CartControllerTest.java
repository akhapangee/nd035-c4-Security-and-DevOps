package com.example.demo.controllers;

import com.example.demo.SampleData;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
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
        user = SampleData.getSampleUser();
        item = SampleData.getSampleItem();
    }

    @Test
    public void testAddTocart() {
        when(userRepository.findByUsername(any())).thenReturn(user);
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        ModifyCartRequest request = SampleData.getSampleCartRequest();

        ResponseEntity<Cart> response = cartController.addTocart(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(item.getId(), response.getBody().getItems().get(0).getId());

    }

    @Test
    public void test_user_null_addTocart() {
        when(userRepository.findByUsername(any())).thenReturn(null);

        ModifyCartRequest request = SampleData.getSampleCartRequest();

        Assertions.assertThrows(NotFoundException.class, () -> {
            cartController.addTocart(request);
        });

    }

    @Test
    public void test_addTocart_null_item() {
        when(userRepository.findByUsername(any())).thenReturn(user);
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        ModifyCartRequest request = SampleData.getSampleCartRequest();

        Assertions.assertThrows(NotFoundException.class, () -> {
            cartController.addTocart(request);
        });

    }


    @Test
    public void testRemoveFromcart() {
        when(userRepository.findByUsername(any())).thenReturn(user);
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        ModifyCartRequest request = SampleData.getSampleCartRequest();

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(0, response.getBody().getItems().size());
    }

    @Test
    public void testRemoveFromcart_invalid_item() {
        when(userRepository.findByUsername(any())).thenReturn(user);
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        ModifyCartRequest request = SampleData.getSampleCartRequest();

        Assertions.assertThrows(NotFoundException.class, () -> {
            cartController.removeFromcart(request);
        });
    }

    @Test
    public void testRemoveFromcart_but_if_there_is_empty_cart() {
        User mockUser = SampleData.getSampleUser();
        mockUser.getCart().setItems(Collections.emptyList());

        // Mock: there is no items in cart added by the user and saved into database
        when(userRepository.findByUsername(any())).thenReturn(mockUser);

        // Mock: There is valid item id passed in request though
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        ModifyCartRequest request = SampleData.getSampleCartRequest();

        Assertions.assertThrows(NotFoundException.class, () -> {
            cartController.removeFromcart(request);
        });

    }

    @Test
    public void testRemoveFromcart_invalid_request_username() {
        // Mock: request contains invalid username which will result null user
        when(userRepository.findByUsername(any())).thenReturn(null);

        ModifyCartRequest request = SampleData.getSampleCartRequest();

        Assertions.assertThrows(NotFoundException.class, () -> {
            cartController.removeFromcart(request);
        });
    }

}
