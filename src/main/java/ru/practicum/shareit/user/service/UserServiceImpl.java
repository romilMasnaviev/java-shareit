package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.ConflictException;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.handler.ValidationException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserConverter;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserConverter converter;

    @Override
    public User create(UserCreateRequest request) {
        log.info("Создание пользователя {}", request);
        User user = converter.convert(request);
        validEmailForCreateUser(user.getEmail());
        return repository.add(user);
    }

    @Override
    public User get(Long id) {
        log.info("Возврат пользователя с id {}", id);
        idExist(id);
        return repository.get(id);
    }

    @Override
    public User update(UserUpdateRequest request, Long userId) {
        log.info("Обновление пользователя {}", request);
        User user = converter.convert(request);
        idExist(userId);
        validEmailForUpdateUser(user.getEmail(), userId);
        return repository.update(user, userId);
    }

    @Override
    public User delete(Long id) {
        log.info("Удаление пользователя с id {}", id);
        idExist(id);
        return repository.delete(id);
    }

    @Override
    public List<User> getAll() {
        log.info("Возврат всех пользователей");
        return repository.getAll();
    }

    private void validEmailForCreateUser(String email) {
        validEmail(email);
        emailInAnotherUser(repository.getUsers(), email);
    }

    private void validEmailForUpdateUser(String email, Long userId) {
        if (email != null) {
            validEmail(email);
            Map<Long, User> users = new HashMap<>(repository.getUsers());
            users.remove(userId);
            emailInAnotherUser(users, email);
        }
    }

    private void emailInAnotherUser(Map<Long, User> userHashMap, String email) {
        if (userHashMap.values().stream().anyMatch(user -> email.equals(user.getEmail()))) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
    }

    private void idExist(Long id) {
        if (repository.getAll().stream().noneMatch(user -> id.equals(user.getId()))) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
    }

    private void validEmail(String email) {
        if (email == null || email.indexOf('@') == -1) {
            throw new ValidationException("Некорректная почта пользователя");
        }
    }

}
