package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UserCreateRequest {
    private String name;
    private String email;
}