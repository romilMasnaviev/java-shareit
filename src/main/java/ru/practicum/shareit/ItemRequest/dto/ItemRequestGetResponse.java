package ru.practicum.shareit.ItemRequest.dto;

import ru.practicum.shareit.item.dto.ItemGetItemRequest;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestGetResponse {
    List<ItemGetItemRequest> items;
    private String description;
    private LocalDateTime created;
}
