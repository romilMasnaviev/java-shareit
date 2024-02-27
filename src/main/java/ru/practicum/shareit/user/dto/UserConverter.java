package ru.practicum.shareit.user.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserConverter {
    UserResponse convert(User user);

    User convert(UserCreateRequest request);

    User convert(UserUpdateRequest request);

    List<UserResponse> convert(List<User> users);

}
