package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserServiceImplIntegrationTest {

    @Autowired
    UserServiceImpl userService;

    @Test
    @DirtiesContext
    public void testGetUser_ValidData_ReturnUser() {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("email@mail.ru");
        request.setName("name");
        userService.create(request);

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(1L);
        expectedResponse.setName(request.getName());
        expectedResponse.setEmail(request.getEmail());

        assertEquals(expectedResponse, userService.get(1L));
    }

    @Test
    @DirtiesContext
    public void testUpdateUser_ValidData_ReturnUpdatedUser() {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("email@mail.ru");
        request.setName("name");
        userService.create(request);

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setName("updatedName");
        updateRequest.setEmail("updatedemail@mail.ru");
        userService.update(updateRequest, 1L);

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(1L);
        expectedResponse.setName(updateRequest.getName());
        expectedResponse.setEmail(updateRequest.getEmail());

        assertEquals(expectedResponse, userService.get(1L));
    }

    @Test
    @DirtiesContext
    public void testDeleteUser_ValidData_ReturnDeletedUser() {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("email@mail.ru");
        request.setName("name");
        userService.create(request);

        UserResponse deletedUser = userService.delete(1L);

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(1L);
        expectedResponse.setName(request.getName());
        expectedResponse.setEmail(request.getEmail());

        assertEquals(expectedResponse, deletedUser);
        assertThrows(NotFoundException.class, () -> userService.get(1L));
    }


}