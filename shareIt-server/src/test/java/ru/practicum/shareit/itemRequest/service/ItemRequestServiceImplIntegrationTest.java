package ru.practicum.shareit.itemRequest.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.itemRequest.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.itemRequest.dto.ItemRequestCreateResponse;
import ru.practicum.shareit.itemRequest.dto.ItemRequestGetResponse;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    ItemRequestServiceImpl itemRequestService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    ItemServiceImpl itemService;

    @Test
    @DirtiesContext
    public void testCreateItemRequest_ValidData_ReturnItemRequestCreateResponse() {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail("email@mail.ru");
        userCreateRequest.setName("name");
        userService.create(userCreateRequest);

        ItemRequestCreateRequest request = new ItemRequestCreateRequest();
        request.setDescription("Request for a new coffee machine");

        ItemRequestCreateResponse response = itemRequestService.create(request, 1L);

        assertNotNull(response.getId());
        assertEquals(request.getDescription(), response.getDescription());
    }

    @Test
    @DirtiesContext
    public void testGetUserItemRequestsFirst_ValidData_ReturnItemRequestGetResponseList() {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail("email@mail.ru");
        userCreateRequest.setName("name");
        userService.create(userCreateRequest);

        ItemRequestCreateRequest request1 = new ItemRequestCreateRequest();
        request1.setDescription("Request 1");
        itemRequestService.create(request1, 1L);

        ItemRequestCreateRequest request2 = new ItemRequestCreateRequest();
        request2.setDescription("Request 2");
        itemRequestService.create(request2, 1L);

        ItemRequestCreateRequest request3 = new ItemRequestCreateRequest();
        request3.setDescription("Request 3");
        itemRequestService.create(request3, 1L);

        List<ItemRequestGetResponse> userItemRequests = itemRequestService.getUserItemRequests(1L);

        assertEquals(3, userItemRequests.size());
        assertTrue(userItemRequests.stream().allMatch(request -> request.getDescription().startsWith("Request")));
    }

    @Test
    @DirtiesContext
    public void testGetUserItemRequestsSecond_ValidData_ReturnItemRequestGetResponseList() {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail("email@mail.ru");
        userCreateRequest.setName("name");
        userService.create(userCreateRequest);

        UserCreateRequest userCreateRequest1 = new UserCreateRequest();
        userCreateRequest1.setEmail("email1@mail.ru");
        userCreateRequest1.setName("name1");
        userService.create(userCreateRequest1);

        ItemRequestCreateRequest request1 = new ItemRequestCreateRequest();
        request1.setDescription("Request 1");
        itemRequestService.create(request1, 1L);

        ItemRequestCreateRequest request2 = new ItemRequestCreateRequest();
        request2.setDescription("Request 2");
        itemRequestService.create(request2, 1L);

        ItemRequestCreateRequest request3 = new ItemRequestCreateRequest();
        request3.setDescription("Request 3");
        itemRequestService.create(request3, 1L);

        List<ItemRequestGetResponse> userItemRequests = itemRequestService.getUserItemRequests(2L, 0L, 10L);

        assertEquals(3, userItemRequests.size());
        assertTrue(userItemRequests.stream().allMatch(request -> request.getDescription().startsWith("Request")));
    }

    @Test
    @DirtiesContext
    public void testGetRequest_ValidData_ReturnItemRequestGetResponse() {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail("email@mail.ru");
        userCreateRequest.setName("name");
        userService.create(userCreateRequest);

        ItemRequestCreateRequest request = new ItemRequestCreateRequest();
        request.setDescription("Request for a new coffee machine");
        ItemRequestCreateResponse createResponse = itemRequestService.create(request, 1L);

        ItemRequestGetResponse response = itemRequestService.getRequest(1L, createResponse.getId());

        assertEquals(request.getDescription(), response.getDescription());
    }

}