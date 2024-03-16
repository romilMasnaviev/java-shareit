package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User create(UserCreateRequest request);

    User get(Long id);

    User update(UserUpdateRequest request, Long id);

    User delete(Long id);

    List<User> getAll();


}
