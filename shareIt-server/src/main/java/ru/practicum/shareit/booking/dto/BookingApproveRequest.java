package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.mapstruct.Mapper;

import javax.validation.constraints.NotNull;

@Mapper(componentModel = "spring")
@Getter
@ToString
@Data
public class BookingApproveRequest {
    @NotNull
    private Long bookingId;
    @NotNull
    private Long userId;
    @NotNull
    private Boolean isApproved;
}