package com.example.demo.controllers;

import com.example.demo.exception.ApiRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;


    @PostMapping("/submit/{username}")
    public ResponseEntity<UserOrder> submit(@PathVariable String username) {
        log.info("Submitting order for user: {}", username);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("User '{}' not found to submit order.", username);
            throw new ApiRequestException(String.format("User '%s' not found to submit order.", username));
        }

        List<Item> items = user.getCart().getItems();
        // Do not submit empty cart
        if (items.isEmpty()) {
            log.error("No items in the cart for user: {}", user.getUsername());
            throw new NotFoundException(String.format("No items in the cart for user: %s", user.getUsername()));
        }

        UserOrder order = UserOrder.createFromCart(user.getCart());
        orderRepository.save(order);
        log.info("Order submitted for user: {} successfully.", username);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
        log.info("Retrieving orders for user: {}", username);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("'{}' not found to get user order history.", username);
            throw new ApiRequestException(String.format("User '%s' not found to get user order history.", username));
        }
        return ResponseEntity.ok(orderRepository.findByUser(user));
    }
}
