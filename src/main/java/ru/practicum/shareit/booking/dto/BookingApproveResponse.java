package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Mapper(componentModel = "spring")
public class BookingApproveResponse {
    Long id;
    Item item;
    User booker;
    Status status;
    LocalDateTime start;
    LocalDateTime end;
}
