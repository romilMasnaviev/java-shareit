package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface InMemoryUserRepository {

    User add(User user);

    User get(Long id);

    User update(User user, Long id);

    User delete(Long id);

    List<User> getAll();

    Map<Long, User> getUsers();

}
