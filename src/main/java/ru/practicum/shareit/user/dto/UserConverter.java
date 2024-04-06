package ru.practicum.shareit.user.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserConverter {

    User userCreateRequestConvertToUser(UserCreateRequest request);

    User userUpdateRequestConvertToUser(UserUpdateRequest request);

    UserResponse userConvertToUserResponse(User user);

    List<UserResponse> userConvertToUserResponse(List<User> users);

}
