package ru.practicum.shareit.booking.dto;

import lombok.Data;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;

@Data
@Mapper(componentModel = "spring")
public class BookingCreateRequest {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}