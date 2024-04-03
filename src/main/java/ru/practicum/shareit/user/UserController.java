package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor

public class UserController {

    private final UserService userService;

    @PostMapping
    UserResponse create(@RequestBody UserCreateRequest request) {
        return userService.create(request);
    }

    @PatchMapping("/{userId}")
    UserResponse update(@RequestBody UserUpdateRequest request, @PathVariable Long userId) {
        return userService.update(request, userId);
    }

    @GetMapping("/{userId}")
    UserResponse get(@PathVariable Long userId) {
        return userService.get(userId);
    }

    @DeleteMapping("/{userId}")
    UserResponse delete(@PathVariable Long userId) {
        return userService.delete(userId);
    }

    @GetMapping
    List<UserResponse> getAll() {
        return userService.getAll();
    }
}
