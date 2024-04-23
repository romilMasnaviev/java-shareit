package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserConverterTest {

    @Autowired
    UserConverter userConverter;

    @Test
    public void testUserConvertToUserResponse_WithNonNullFields_ReturnsUserResponse() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        UserResponse response = userConverter.userConvertToUserResponse(user);

        assertNotNull(response);

        assertEquals(user.getId(), response.getId());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    public void testUserConvertToUserResponse_WithNullFields_ReturnsUserResponseWithNullValues() {
        User user = new User();

        UserResponse response = userConverter.userConvertToUserResponse(user);

        assertNotNull(response);

        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getEmail());
    }

    @Test
    public void testUserConvertToUserResponse_WithNonNullFields_ReturnsUserResponseList() {
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setName("John Doe");
        user1.setEmail("john.doe@example.com");
        userList.add(user1);

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Smith");
        user2.setEmail("jane.smith@example.com");
        userList.add(user2);

        List<UserResponse> responseList = userConverter.userConvertToUserResponse(userList);

        assertNotNull(responseList);

        assertEquals(userList.size(), responseList.size());

        for (int i = 0; i < userList.size(); i++) {
            UserResponse response = responseList.get(i);
            User user = userList.get(i);
            assertEquals(user.getId(), response.getId());
            assertEquals(user.getName(), response.getName());
            assertEquals(user.getEmail(), response.getEmail());
        }
    }

    @Test
    public void testUserConvertToUserResponse_WithEmptyList_ReturnsEmptyList() {
        List<User> userList = new ArrayList<>();

        List<UserResponse> responseList = userConverter.userConvertToUserResponse(userList);

        assertNotNull(responseList);

        assertTrue(responseList.isEmpty());
    }

    @Test
    void userCreateRequestConvertToUser_NullRequest_ReturnsNull() {
        UserCreateRequest request = null;

        User result = userConverter.userCreateRequestConvertToUser(request);

        assertNull(result);
    }

    @Test
    void userUpdateRequestConvertToUser_NullRequest_ReturnsNull() {
        UserUpdateRequest request = null;

        User result = userConverter.userUpdateRequestConvertToUser(request);

        assertNull(result);
    }

    @Test
    void userConvertToUserResponse_NullUser_ReturnsNull() {
        User user = null;

        UserResponse result = userConverter.userConvertToUserResponse(user);

        assertNull(result);
    }

    @Test
    void userConvertToUserResponse_NullList_ReturnsEmptyList() {
        List<User> users = null;

        List<UserResponse> result = userConverter.userConvertToUserResponse(users);

        assertNull(result);
    }
}