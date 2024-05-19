package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingApproveResponse;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingCreateResponse;
import ru.practicum.shareit.booking.dto.BookingGetResponse;

import java.util.List;

public interface BookingService {

    BookingCreateResponse create(BookingCreateRequest request, Long itemId, Long userId);

    BookingApproveResponse approve(Long bookingId, Long userId, Boolean isApproved);

    BookingGetResponse get(Long bookingId, Long userId);

    List<BookingGetResponse> getOwnerBookings(Long userId, String state, Long from, Long size);

    List<BookingGetResponse> getUserBookings(Long userId, String state, Long from, Long size);
}
