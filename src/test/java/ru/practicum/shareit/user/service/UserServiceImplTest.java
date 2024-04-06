package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserConverter;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConverter userConverter;

    @InjectMocks
    private UserServiceImpl userService;

    private static List<UserResponse> getUserResponses(User user1, User user2) {
        List<UserResponse> userResponseList = new ArrayList<>();
        UserResponse userResponse1 = new UserResponse();
        userResponse1.setId(user1.getId());
        userResponse1.setName(user1.getName());
        userResponse1.setEmail(user1.getEmail());
        userResponseList.add(userResponse1);

        UserResponse userResponse2 = new UserResponse();
        userResponse2.setId(user2.getId());
        userResponse2.setName(user2.getName());
        userResponse2.setEmail(user2.getEmail());
        userResponseList.add(userResponse2);
        return userResponseList;
    }

    @BeforeEach
    public void setup() {
        userService = new UserServiceImpl(userRepository, userConverter);
    }

    @Test
    public void testCreate_ValidData_Success() {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("name");
        request.setEmail("mail@mail.ru");

        User user = new User();
        user.setId(1L);
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(user.getId());
        expectedResponse.setName(user.getName());
        expectedResponse.setEmail(user.getEmail());

        when(userConverter.convert(request)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userConverter.convert(user)).thenReturn(expectedResponse);

        UserResponse actualResponse = userService.create(request);

        verify(userConverter).convert(request);
        verify(userRepository).save(user);
        verify(userConverter).convert(user);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGet_UserNotFound_ThrowsException() {
        Long userId = 123L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.get(userId));
    }

    @Test
    public void testGet_UserFound_ReturnsUserResponse() {
        Long userId = 123L;
        User user = new User();
        user.setId(userId);
        user.setName("name");
        user.setEmail("email@mail.ru");
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(userId);
        expectedResponse.setName(user.getName());
        expectedResponse.setEmail(user.getEmail());
        when(userConverter.convert(user)).thenReturn(expectedResponse);

        UserResponse actualResponse = userService.get(userId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testUpdate_UserNotFound_ThrowsException() {
        Long userId = 123L;
        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("name");
        request.setEmail("mail@mail.ru");
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.update(request, userId));
    }

    @Test
    public void testUpdate_ValidRequest_ReturnsUpdatedUser() {
        Long userId = 123L;
        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John");
        existingUser.setEmail("john@example.com");

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.getReferenceById(userId)).thenReturn(existingUser);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName(request.getName());
        updatedUser.setEmail(request.getEmail());

        when(userConverter.convert(request)).thenReturn(updatedUser);

        User savedUser = new User();
        savedUser.setId(userId);
        savedUser.setName(request.getName());
        savedUser.setEmail(request.getEmail());

        when(userRepository.save(existingUser)).thenReturn(savedUser);

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(userId);
        expectedResponse.setName(request.getName());
        expectedResponse.setEmail(request.getEmail());

        when(userConverter.convert(savedUser)).thenReturn(expectedResponse);

        UserResponse actualResponse = userService.update(request, userId);

        assertNotNull(actualResponse);
        assertEquals(userId, actualResponse.getId());
        assertEquals(request.getName(), actualResponse.getName());
        assertEquals(request.getEmail(), actualResponse.getEmail());

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).getReferenceById(userId);
        verify(userRepository, times(1)).save(existingUser);
        verify(userConverter, times(1)).convert(request);
        verify(userConverter, times(1)).convert(savedUser);
    }

    @Test
    public void testDelete_ValidData_MethodsCalledCorrectly() {
        Long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John");
        existingUser.setEmail("john@example.com");

        when(userRepository.existsById(userId)).thenReturn(true);

        when(userRepository.getReferenceById(userId)).thenReturn(existingUser);

        UserResponse deletedUserResponse = new UserResponse();
        deletedUserResponse.setEmail(existingUser.getEmail());
        deletedUserResponse.setName(existingUser.getName());
        deletedUserResponse.setId(userId);

        when(userConverter.convert(existingUser)).thenReturn(deletedUserResponse);
        doNothing().when(userRepository).deleteById(userId);


        UserResponse actualResponse = userService.delete(userId);

        assertNotNull(actualResponse);
        assertEquals(userId, actualResponse.getId());
        assertEquals(existingUser.getName(), actualResponse.getName());
        assertEquals(existingUser.getEmail(), actualResponse.getEmail());
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).getReferenceById(userId);
        verify(userRepository, times(1)).deleteById(userId);
        verify(userConverter, times(1)).convert(existingUser);
    }

    @Test
    public void testDelete_UserDoesNotExist_ThrowsException() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.delete(userId));

        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    void testGetAll_ReturnsListOfUsers() {
        // Arrange
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setName("John");
        user1.setEmail("john@example.com");
        userList.add(user1);

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane");
        user2.setEmail("jane@example.com");
        userList.add(user2);

        when(userRepository.findAll()).thenReturn(userList);

        List<UserResponse> userResponseList = getUserResponses(user1, user2);

        when(userConverter.convert(userList)).thenReturn(userResponseList);

        List<UserResponse> actualResponseList = userService.getAll();

        assertNotNull(actualResponseList);
        assertEquals(2, actualResponseList.size());
        assertEquals(userResponseList, actualResponseList);
    }

    @Test
    void testGetAll_ReturnsEmptyListWhenNoUsers() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        when(userConverter.convert(new ArrayList<>())).thenReturn(new ArrayList<>());

        List<UserResponse> actualResponseList = userService.getAll();

        assertNotNull(actualResponseList);
        assertTrue(actualResponseList.isEmpty());
    }

    @Test
    void testCheckUserDoesntExistAndThrowIfNotFound_UserExists() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        assertDoesNotThrow(() -> userService.checkUserDoesntExistAndThrowIfNotFound(userId));
    }

    @Test
    void testCheckUserDoesntExistAndThrowIfNotFound_UserDoesNotExist() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.checkUserDoesntExistAndThrowIfNotFound(userId));
        assertEquals("User not found with ID: " + userId, exception.getMessage());
    }

}