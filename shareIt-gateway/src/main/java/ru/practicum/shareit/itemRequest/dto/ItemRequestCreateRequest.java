package ru.practicum.shareit.itemRequest.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ItemRequestCreateRequest {
    @NotEmpty
    private String description;
}