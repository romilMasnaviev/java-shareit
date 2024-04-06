package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemCreateResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
