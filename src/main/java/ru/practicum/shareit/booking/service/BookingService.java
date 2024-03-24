package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking create(BookingCreateRequest request, Long itemId, Long userId);

    Booking approve(Long bookingId, Long userId, Boolean isApproved);

    Booking get(Long bookingId, Long userId);

    List<Booking> getUserBookings(Long userId, String state);

    List<Booking> getOwnerBookings(Long userId, String state);
}
