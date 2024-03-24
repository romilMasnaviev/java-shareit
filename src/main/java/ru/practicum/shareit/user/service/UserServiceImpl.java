package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.user.dao.JpaUserRepository;
import ru.practicum.shareit.user.dto.UserConverter;
import ru.practicum.shareit.user.dto.UserCreateRequest;
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

    private final JpaUserRepository repository;
    private final UserConverter converter;

    @Override
    public User create(@Valid UserCreateRequest request) {
        log.info("Creating user: {}", request);
        User user = converter.convert(request);
        return repository.save(user);
    }

    @Override
    public User get(Long id) {
        log.info("Retrieving user with ID: {}", id);
        return repository.getReferenceById(id);
    }

    @Override
    public User update(UserUpdateRequest request, Long userId) {
        log.info("Updating user: {}", request);
        User newUser = converter.convert(request);
        User oldUser = repository.getReferenceById(userId);
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        return repository.save(oldUser);
    }

    @Override
    public User delete(Long id) {
        log.info("Deleting user with ID: {}", id);
        User user = repository.getReferenceById(id);
        repository.deleteById(id);
        return user;
    }

    @Override
    public List<User> getAll() {
        log.info("Retrieving all users");
        return repository.findAll();
    }

}
