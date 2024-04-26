package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

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