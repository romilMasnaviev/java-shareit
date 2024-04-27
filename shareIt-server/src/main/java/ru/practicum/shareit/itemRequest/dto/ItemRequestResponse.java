package ru.practicum.shareit.itemRequest.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemGetItemResponse;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestResponse {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemGetItemResponse> items;
}