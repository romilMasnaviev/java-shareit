package ru.practicum.shareit.booking.dto;

import lombok.Data;
import org.mapstruct.Mapper;

@Data
@Mapper(componentModel = "spring")
public class BookingApproveRequest {
    private Long bookingId;
    private Long userId;
    private Boolean isApproved;
}