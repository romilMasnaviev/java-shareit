package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testCreateUser_validData_returnUserResponse() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("name");
        request.setEmail("mail@mail.ru");

        when(userService.create(any(UserCreateRequest.class)))
                .thenAnswer(invocation -> {
                    UserResponse userResponse = new UserResponse();
                    userResponse.setId(1L);
                    userResponse.setEmail(request.getEmail());
                    userResponse.setName(request.getName());
                    return userResponse;
                });

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is(request.getName())))
                .andExpect(jsonPath("$.email", is(request.getEmail())));
    }

    @Test
    public void testUpdateUser_validData_returnUserResponse() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("name");
        request.setEmail("mail@mail.ru");

        when(userService.update(any(UserUpdateRequest.class), anyLong()))
                .thenAnswer(invocation -> {
                    UserResponse userResponse = new UserResponse();
                    userResponse.setId(1L);
                    userResponse.setEmail(request.getEmail());
                    userResponse.setName(request.getName());
                    return userResponse;
                });

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is(request.getName())))
                .andExpect(jsonPath("$.email", is(request.getEmail())));
    }

    @Test
    public void testGetUser_validData_returnUserResponse() throws Exception {
        Long userId = 1L;
        UserResponse response = new UserResponse();
        when(userService.get(anyLong())).thenAnswer(invocation -> {
            response.setName("name");
            response.setEmail("email@mail.ru");
            response.setId(1L);
            return response;
        });

        mvc.perform(get("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.email", is(response.getEmail())))
                .andExpect(jsonPath("$.name", is(response.getName())));
    }

    @Test
    public void testDeleteUser_validData_returnUserResponse() throws Exception {
        Long userId = 1L;
        UserResponse response = new UserResponse();
        when(userService.delete(anyLong())).thenAnswer(invocation -> {
            response.setName("name");
            response.setEmail("email@mail.ru");
            response.setId(1L);
            return response;
        });

        mvc.perform(delete("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.email", is(response.getEmail())))
                .andExpect(jsonPath("$.name", is(response.getName())));
    }

    @Test
    public void testGetAllUser_validData_returnUserResponse() throws Exception {
        UserResponse response = new UserResponse();
        when(userService.getAll()).thenAnswer(invocation -> {
            response.setName("name");
            response.setEmail("email@mail.ru");
            response.setId(1L);
            return List.of(response);
        });

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$.[0].email", is(response.getEmail())))
                .andExpect(jsonPath("$.[0].name", is(response.getName())));
    }


}