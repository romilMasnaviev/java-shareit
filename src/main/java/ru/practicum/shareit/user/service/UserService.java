package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

import javax.validation.Valid;
import java.util.List;

public interface UserService {
    UserResponse create(@Valid UserCreateRequest request);

    UserResponse get(Long userId);

    UserResponse update(UserUpdateRequest request, Long userId);

    UserResponse delete(Long userId);

    List<UserResponse> getAll();

    void checkUserExistsAndThrowIfNotFound(Long userId);
}
