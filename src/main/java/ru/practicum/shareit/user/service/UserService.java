package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

public interface UserService {
    User create(@Valid UserCreateRequest request);

    User get(Long id);

    User update(UserUpdateRequest request, Long id);

    User delete(Long id);

    List<User> getAll();


}
