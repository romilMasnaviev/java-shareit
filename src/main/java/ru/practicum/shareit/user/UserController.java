package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserConverter;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService service;
    @Autowired
    private final UserConverter converter;

    @PostMapping
    UserResponse create(@RequestBody UserCreateRequest request) {
        return converter.convert(service.create(request));
    }

    @PatchMapping("/{id}")
    UserResponse update(@RequestBody UserUpdateRequest request, @PathVariable Long id) {
        return converter.convert(service.update(request, id));
    }

    @GetMapping("/{id}")
    UserResponse get(@PathVariable Long id) {
        return converter.convert(service.get(id));
    }

    @DeleteMapping("/{id}")
    UserResponse delete(@PathVariable Long id) {
        return converter.convert(service.delete(id));
    }

    @GetMapping
    List<UserResponse> getAll() {
        return converter.convert(service.getAll());
    }
}
