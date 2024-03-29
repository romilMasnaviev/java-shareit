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

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    @Override
    public UserResponse create(@Valid UserCreateRequest request) {
        log.info("Creating user: {}", request);
        User user = userConverter.convert(request);
        return userConverter.convert(userRepository.save(user));
    }

    @Override
    public UserResponse get(Long userId) {
        log.info("Retrieving user with ID: {}", userId);
        checkUserExistsAndThrowIfNotFound(userId);
        return userConverter.convert(userRepository.getReferenceById(userId));
    }

    @Override
    public UserResponse update(UserUpdateRequest request, Long userId) {
        log.info("Updating user with ID: {}, request: {}", userId, request);
        checkUserExistsAndThrowIfNotFound(userId);
        User updatedUser = userConverter.convert(request);
        User existingUser = userRepository.getReferenceById(userId);
        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        return userConverter.convert(userRepository.save(existingUser));
    }

    @Override
    public UserResponse delete(Long userId) {
        log.info("Deleting user with ID: {}", userId);
        checkUserExistsAndThrowIfNotFound(userId);
        User deletedUser = userRepository.getReferenceById(userId);
        userRepository.deleteById(userId);
        return userConverter.convert(deletedUser);
    }

    @Override
    public List<UserResponse> getAll() {
        log.info("Retrieving all users");
        return userConverter.convert(userRepository.findAll());
    }

    @Override
    public void checkUserExistsAndThrowIfNotFound(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found with ID: " + userId);
        }
    }

}
