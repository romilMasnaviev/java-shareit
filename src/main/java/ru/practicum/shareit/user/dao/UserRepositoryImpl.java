package ru.practicum.shareit.user.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    @Getter
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public User add(User user) {
        user.setId(id);
        users.put(id, user);
        return users.get(id++);
    }

    @Override
    public User update(User user, Long userId) {
        if (user.getName() != null) users.get(userId).setName(user.getName());
        if (user.getEmail() != null) users.get(userId).setEmail(user.getEmail());
        return users.get(userId);
    }

    @Override
    public User delete(Long id) {
        return users.remove(id);
    }

    @Override
    public User get(Long id) {
        return users.get(id);
    }

    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

}
