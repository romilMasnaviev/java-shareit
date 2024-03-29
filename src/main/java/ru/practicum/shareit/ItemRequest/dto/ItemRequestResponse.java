package ru.practicum.shareit.ItemRequest.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemGetItemRequest;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestResponse {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemGetItemRequest> items;
}
