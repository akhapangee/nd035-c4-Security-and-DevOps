package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.SampleData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    public void test_user_null_addTocart() {
        when(userRepository.findByUsername(any())).thenReturn(null);

        ModifyCartRequest request = SampleData.getSampleCartRequest();

        ResponseEntity<Cart> response = cartController.addTocart(request);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    public void test_item_not_found_addTocart() {
        when(userRepository.findByUsername(any())).thenReturn(user);
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        ModifyCartRequest request = SampleData.getSampleCartRequest();

        ResponseEntity<Cart> response = cartController.addTocart(request);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

}
