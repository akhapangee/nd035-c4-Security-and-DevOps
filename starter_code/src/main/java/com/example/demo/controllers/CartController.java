package com.example.demo.controllers;

import com.example.demo.exception.NotFoundException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    public static final Logger log = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    @PostMapping("/addToCart")
    public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
        log.info("Adding item to cart item ID: {}, quantity: {} for user: {}", request.getItemId(), request.getQuantity(), request.getUsername());
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throwUserNotFoundException(request);
        }
        Optional<Item> item = itemRepository.findById(request.getItemId());
        if (!item.isPresent()) {
            throwItemNotFoundException(request);
        }
        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity())
                .forEach(i -> cart.addItem(item.get()));
        cartRepository.save(cart);
        log.info("Added item to the cart successfully.");
        return new ResponseEntity<>(cart, HttpStatus.CREATED);
    }

    @PostMapping("/removeFromCart")
    public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
        log.info("Removing item from cart item ID: {}, quantity: {} for user: {}", request.getItemId(), request.getQuantity(), request.getUsername());

        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throwUserNotFoundException(request);
        }
        Optional<Item> item = itemRepository.findById(request.getItemId());
        if (!item.isPresent()) {
            throwItemNotFoundException(request);
        }
        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity())
                .forEach(i -> cart.removeItem(item.get()));
        cartRepository.save(cart);
        log.info("Removed item: {} from the cart successfully.", item.get().getId());
        return ResponseEntity.ok(cart);
    }

    private static void throwItemNotFoundException(ModifyCartRequest request) {
        log.error("Item ID '{}' not found.", request.getItemId());
        throw new NotFoundException(String.format("Item ID '%s' not found.", request.getItemId()));
    }

    private static void throwUserNotFoundException(ModifyCartRequest request) {
        log.error("Username '{}' not found.", request.getUsername());
        throw new NotFoundException(String.format("Username '%s' not found.", request.getUsername()));
    }

}
