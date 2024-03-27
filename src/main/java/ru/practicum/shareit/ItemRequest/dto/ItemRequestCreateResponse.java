package ru.practicum.shareit.ItemRequest.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestCreateResponse {
    private Long id;
    private String description;
    private LocalDateTime created;
}
