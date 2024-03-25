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

    @PatchMapping("/{id}")
    UserResponse update(@RequestBody UserUpdateRequest request, @PathVariable Long id) {
        return userService.update(request, id);
    }

    @GetMapping("/{id}")
    UserResponse get(@PathVariable Long id) {
        return userService.get(id);
    }

    @DeleteMapping("/{id}")
    UserResponse delete(@PathVariable Long id) {
        return userService.delete(id);
    }

    @GetMapping
    List<UserResponse> getAll() {
        return userService.getAll();
    }
}
