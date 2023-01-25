package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.samples.SampleData;
import org.assertj.core.util.Lists;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemController itemController;

    private static Item item;

    @BeforeAll
    public static void beforeAll() {
        item = SampleData.getSampleItem();
    }

    @Test
    public void getItemById() {
        given(itemRepository.findById(any())).willReturn(Optional.of(item));
        ResponseEntity<Item> response = itemController.getItemById(item.getId());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(item.getId(), response.getBody().getId());
    }

    @Test
    public void getItemsByName() {
        given(itemRepository.findByName(anyString())).willReturn(Collections.singletonList(item));

        ResponseEntity<List<Item>> response = itemController.getItemsByName(item.getName());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(Lists.newArrayList(item), response.getBody());
    }

    @Test
    public void test_getItems() {
        given(itemRepository.findAll()).willReturn(Collections.singletonList(item));

        ResponseEntity<List<Item>> response = itemController.getItems();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(Lists.newArrayList(item), response.getBody());
    }

}
