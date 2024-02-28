package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItemCreateRequest {
    private String name;
    private String description;
    private Boolean available;
}
