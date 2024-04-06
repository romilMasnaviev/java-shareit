package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class BookingApproveResponse {
    Long id;
    Item item;
    User booker;
    Status status;
    LocalDateTime start;
    LocalDateTime end;
}
