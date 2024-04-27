package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class BookingCreateRequest {
    private long itemId;
    @FutureOrPresent @NotNull
    private LocalDateTime start;
    @Future @NotNull
    private LocalDateTime end;
}