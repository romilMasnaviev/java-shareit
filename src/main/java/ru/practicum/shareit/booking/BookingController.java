package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingApproveResponse;
import ru.practicum.shareit.booking.dto.BookingConverter;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private static final String xSharerUserId = "X-Sharer-User-Id";

    private final BookingServiceImpl bookingService;
    private final BookingConverter bookingConverter;

    @PostMapping
    Booking create(@RequestBody @Valid BookingCreateRequest request, @RequestHeader(xSharerUserId) Long userId) {
        return bookingService.create(bookingConverter.convert(request), request.getItemId(), userId);
    }

    @PatchMapping("/{bookingId}")
    BookingApproveResponse approve(@PathVariable Long bookingId, @RequestHeader(xSharerUserId) Long userId,
                                   @RequestParam(name = "approved") Boolean isApproved) {
        return bookingService.approve(bookingId,userId,isApproved);
    }


}
