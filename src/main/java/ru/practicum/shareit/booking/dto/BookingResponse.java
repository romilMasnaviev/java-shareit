package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Mapper(componentModel = "spring")
public class BookingResponse {
    Long id;
    Item item;
    User booker;
    Status status;
    LocalDateTime start;
    LocalDateTime end;
    Long bookerId;

    public Long getBookerId() { //TODO заменить
        return booker != null ? booker.getId() : null;
    }
}
