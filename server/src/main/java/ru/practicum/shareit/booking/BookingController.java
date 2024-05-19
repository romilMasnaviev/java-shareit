package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingApproveResponse;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingCreateResponse;
import ru.practicum.shareit.booking.dto.BookingGetResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.utility.ControllerConstants.xSharerUserId;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingCreateResponse create(@RequestBody BookingCreateRequest request, @RequestHeader(xSharerUserId) Long userId) {
        return bookingService.create(request, request.getItemId(), userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingApproveResponse approve(@PathVariable Long bookingId, @RequestHeader(xSharerUserId) Long userId,
                                          @RequestParam(name = "approved") Boolean isApproved) {
        return bookingService.approve(bookingId, userId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingGetResponse get(@PathVariable Long bookingId, @RequestHeader(xSharerUserId) Long userId) {
        return bookingService.get(bookingId, userId);
    }

    @GetMapping
    public List<BookingGetResponse> getUserBookings(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                    @RequestHeader(xSharerUserId) Long userId,
                                                    @RequestParam(required = false, name = "from") Long from,
                                                    @RequestParam(required = false, name = "size") Long size) {
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingGetResponse> getOwnerBookings(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                     @RequestHeader(xSharerUserId) Long userId,
                                                     @RequestParam(required = false, name = "from") Long from,
                                                     @RequestParam(required = false, name = "size") Long size) {
        return bookingService.getOwnerBookings(userId, state, from, size);
    }
}
