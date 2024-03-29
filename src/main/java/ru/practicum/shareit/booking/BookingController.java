package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private static final String xSharerUserId = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    BookingResponse create(@RequestBody @Valid BookingCreateRequest request, @RequestHeader(xSharerUserId) Long userId) {
        return bookingService.create(request, request.getItemId(), userId);
    }

    @PatchMapping("/{bookingId}")
    BookingResponse approve(@PathVariable Long bookingId, @RequestHeader(xSharerUserId) Long userId,
                            @RequestParam(name = "approved") Boolean isApproved) {
        return bookingService.approve(bookingId, userId, isApproved);
    }

    @GetMapping("/{bookingId}")
    BookingResponse getBookingById(@PathVariable Long bookingId, @RequestHeader(xSharerUserId) Long userId) {
        return bookingService.get(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponse> getUserBookings(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                 @RequestHeader(xSharerUserId) Long userId,
                                                 @RequestParam(required = false, name = "from") Long from,
                                                 @RequestParam(required = false, name = "size") Long size) {
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponse> getOwnerBookings(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                  @RequestHeader(xSharerUserId) Long userId,
                                                  @RequestParam(required = false, name = "from") Long from,
                                                  @RequestParam(required = false, name = "size") Long size) {
        return bookingService.getOwnerBookingsHub(userId, state, from, size);
    }
}
