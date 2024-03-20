package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {

    Booking create(Booking booking, Long itemId, Long userId);

    Booking approve(Long bookingId, Long userId, Boolean isApproved);

    Booking get(Long bookingId, Long userId);

    List<Booking> getUserBookings(Long userId, String state);

    List<Booking> getOwnerBookings(Long userId, String state);
}
