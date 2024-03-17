package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingApproveResponse;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingService {

    Booking create(Booking booking, Long itemId, Long userId);

    BookingApproveResponse approve(Long bookingId, Long userId, Boolean isApproved);
}
