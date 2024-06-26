package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.handler.ConflictException;
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
        User user = userConverter.userCreateRequestConvertToUser(request);
        return userConverter.userConvertToUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse get(Long userId) {
        log.info("Retrieving user with ID: {}", userId);
        checkUserDoesntExistAndThrowIfNotFound(userId);
        return userConverter.userConvertToUserResponse(userRepository.getReferenceById(userId));
    }

    @Override
    public UserResponse update(UserUpdateRequest request, Long userId) {
        log.info("Updating user with ID: {}, request: {}", userId, request);
        checkUserDoesntExistAndThrowIfNotFound(userId);
        checkUserAlreadyExistsByEmailAndThrowIfFound(request.getEmail(), userId);
        User updatedUser = userConverter.userUpdateRequestConvertToUser(request);
        User existingUser = userRepository.getReferenceById(userId);
        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        return userConverter.userConvertToUserResponse(userRepository.save(existingUser));
    }

    @Override
    public UserResponse delete(Long userId) {
        log.info("Deleting user with ID: {}", userId);
        checkUserDoesntExistAndThrowIfNotFound(userId);
        User deletedUser = userRepository.getReferenceById(userId);
        userRepository.deleteById(userId);
        return userConverter.userConvertToUserResponse(deletedUser);
    }

    @Override
    public List<UserResponse> getAll() {
        log.info("Retrieving all users");
        return userConverter.userConvertToUserResponse(userRepository.findAll());
    }

    @Override
    public void checkUserDoesntExistAndThrowIfNotFound(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found with ID: " + userId);
        }
    }

    private void checkUserAlreadyExistsByEmailAndThrowIfFound(String email, Long userId) {
        if (userRepository.existsByEmailAndIdNot(email, userId)) {
            throw new ConflictException("This e-mail is already on another user");
        }
    }

}