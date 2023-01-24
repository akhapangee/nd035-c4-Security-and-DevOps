package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
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
import samples.SampleData;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

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
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        ResponseEntity<Item> response = itemController.getItemById(item.getId());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(item.getId(), response.getBody().getId());
    }

    @Test
    public void getItemsByName() {
        when(itemRepository.findByName(item.getName())).thenReturn(Lists.newArrayList(item));

        ResponseEntity<List<Item>> response = itemController.getItemsByName(item.getName());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(Lists.newArrayList(item), response.getBody());
    }

    @Test
    public void test_getItems() {
        when(itemRepository.findAll()).thenReturn(Lists.newArrayList(item));

        ResponseEntity<List<Item>> response = itemController.getItems();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(Lists.newArrayList(item), response.getBody());
    }

}
