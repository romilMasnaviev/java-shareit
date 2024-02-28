package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
public class ItemResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
