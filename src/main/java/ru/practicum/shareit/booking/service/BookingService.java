package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;

import java.util.List;

public interface BookingService {

    BookingResponse create(BookingCreateRequest request, Long itemId, Long userId);

    BookingResponse approve(Long bookingId, Long userId, Boolean isApproved);

    BookingResponse get(Long bookingId, Long userId);

    List<BookingResponse> getUserBookings(Long userId, String state);

    List<BookingResponse> getOwnerBookings(Long userId, String state);
}
