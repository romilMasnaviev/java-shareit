package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.*;

import java.util.List;

public interface BookingService {

    BookingCreateResponse create(BookingCreateRequest request, Long itemId, Long userId);

    BookingApproveResponse approve(Long bookingId, Long userId, Boolean isApproved);

    BookingGetResponse get(Long bookingId, Long userId);

    List<BookingGetResponse> getOwnerBookingsHub(Long userId, String state, Long from, Long size);

    List<BookingGetResponse> getUserBookings(Long userId, String state, Long from, Long size);
}
