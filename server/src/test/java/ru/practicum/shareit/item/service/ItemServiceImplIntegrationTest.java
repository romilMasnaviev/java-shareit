package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserServiceImpl userService;

    @Test
    @DirtiesContext
    public void testGetItem_ValidData_ReturnItemCreateResponse() {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail("email@mail.ru");
        userCreateRequest.setName("name");
        userService.create(userCreateRequest);

        ItemCreateRequest itemCreateRequest = new ItemCreateRequest();
        itemCreateRequest.setName("itemName");
        itemCreateRequest.setDescription("itemDescription");
        itemCreateRequest.setAvailable(true);
        ItemCreateResponse actualItemCreateResponse = itemService.create(itemCreateRequest, 1L);

        ItemCreateResponse expectedResponse = new ItemCreateResponse();
        expectedResponse.setId(1L);
        expectedResponse.setAvailable(itemCreateRequest.getAvailable());
        expectedResponse.setDescription(itemCreateRequest.getDescription());
        expectedResponse.setName(itemCreateRequest.getName());

        assertEquals(expectedResponse, actualItemCreateResponse);
    }

    @Test
    @DirtiesContext
    public void testUpdateItem_ValidData_ReturnItemUpdateResponse() {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail("email@mail.ru");
        userCreateRequest.setName("name");
        userService.create(userCreateRequest);

        ItemCreateRequest itemCreateRequest = new ItemCreateRequest();
        itemCreateRequest.setName("itemName");
        itemCreateRequest.setDescription("itemDescription");
        itemCreateRequest.setAvailable(true);
        ItemCreateResponse createdItemResponse = itemService.create(itemCreateRequest, 1L);

        ItemUpdateRequest updateRequest = new ItemUpdateRequest();
        updateRequest.setId(createdItemResponse.getId());
        updateRequest.setName("updatedItemName");
        updateRequest.setDescription("updatedItemDescription");
        updateRequest.setAvailable(false);

        ItemUpdateResponse updatedItemResponse = itemService.update(updateRequest, 1L, createdItemResponse.getId());

        assertEquals(createdItemResponse.getId(), updatedItemResponse.getId());
        assertEquals(updateRequest.getName(), updatedItemResponse.getName());
        assertEquals(updateRequest.getDescription(), updatedItemResponse.getDescription());
        assertEquals(updateRequest.getAvailable(), updatedItemResponse.getAvailable());
    }

    @Test
    @DirtiesContext
    public void testGetAllItems_ValidData_ReturnItemGetResponseList() {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail("email@mail.ru");
        userCreateRequest.setName("name");
        userService.create(userCreateRequest);

        int itemCount = 5;
        List<Long> itemIds = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            ItemCreateRequest itemCreateRequest = new ItemCreateRequest();
            itemCreateRequest.setName("Item " + i);
            itemCreateRequest.setDescription("Description for Item " + i);
            itemCreateRequest.setAvailable(true);
            ItemCreateResponse createdItemResponse = itemService.create(itemCreateRequest, 1L);
            itemIds.add(createdItemResponse.getId());
        }

        List<ItemGetResponse> allItemsResponse = itemService.getAll(1L, 0L, (long) itemCount);

        assertEquals(itemCount, allItemsResponse.size());

        for (int i = 0; i < itemCount; i++) {
            ItemGetResponse itemResponse = allItemsResponse.get(i);
            assertEquals(itemIds.get(i), itemResponse.getId());
            assertEquals("Item " + i, itemResponse.getName());
            assertEquals("Description for Item " + i, itemResponse.getDescription());
            assertTrue(itemResponse.getAvailable());
        }
    }

    @Test
    @DirtiesContext
    public void testGetItem_ValidData_ReturnItemGetResponse() {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail("email@mail.ru");
        userCreateRequest.setName("name");
        userService.create(userCreateRequest);

        ItemCreateRequest itemCreateRequest = new ItemCreateRequest();
        itemCreateRequest.setName("itemName");
        itemCreateRequest.setDescription("itemDescription");
        itemCreateRequest.setAvailable(true);
        ItemCreateResponse createdItemResponse = itemService.create(itemCreateRequest, 1L);

        ItemGetResponse itemGetResponse = itemService.get(createdItemResponse.getId(), 1L);

        assertEquals(createdItemResponse.getId(), itemGetResponse.getId());
        assertEquals(itemCreateRequest.getName(), itemGetResponse.getName());
        assertEquals(itemCreateRequest.getDescription(), itemGetResponse.getDescription());
        assertTrue(itemGetResponse.getAvailable());
    }
}