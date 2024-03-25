package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.ToString;
import org.mapstruct.Mapper;

import javax.validation.constraints.NotNull;

@Mapper(componentModel = "spring")
@Getter
@ToString
public class BookingApproveRequest {
    @NotNull
    Long bookingId;
    @NotNull
    Long userId;
    @NotNull
    Boolean isApproved;
}
