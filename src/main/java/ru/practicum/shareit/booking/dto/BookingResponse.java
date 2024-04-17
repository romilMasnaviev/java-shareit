package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class BookingResponse {
    private Long id;
    private Item item;
    private User booker;
    private Status status;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long bookerId;
}