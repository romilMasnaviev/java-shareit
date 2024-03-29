package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserConverter;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
@Validated
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserConverter converter;

    @Override
    public UserResponse create(@Valid UserCreateRequest request) {
        log.info("Creating user: {}", request);
        User user = converter.convert(request);
        return converter.convert(repository.save(user));
    }

    @Override
    public UserResponse get(Long id) {
        log.info("Retrieving user with ID: {}", id);
        checkUserExists(id);
        return converter.convert(repository.getReferenceById(id));
    }

    @Override
    public UserResponse update(UserUpdateRequest request, Long userId) {
        log.info("Updating user with ID: {}, request: {}", userId, request);
        checkUserExists(userId);
        User updatedUser = converter.convert(request);
        User existingUser = repository.getReferenceById(userId);
        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        return converter.convert(repository.save(existingUser));
    }

    @Override
    public UserResponse delete(Long id) {
        log.info("Deleting user with ID: {}", id);
        checkUserExists(id);
        User deletedUser = repository.getReferenceById(id);
        repository.deleteById(id);
        return converter.convert(deletedUser);
    }

    @Override
    public List<UserResponse> getAll() {
        log.info("Retrieving all users");
        return converter.convert(repository.findAll());
    }

    private void checkUserExists(Long userId) {
        if (!repository.existsById(userId)) {
            throw new NotFoundException("User with ID " + userId + " does not exist");
        }
    }
}
