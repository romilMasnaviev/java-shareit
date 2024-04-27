package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.handler.InternalServerException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.utility.ControllerConstants.xSharerUserId;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(xSharerUserId) long userId,
                                         @RequestBody @Valid BookingCreateRequest request) {
        log.info("Creating booking {}, userId={}", request, userId);
        return bookingClient.bookItem(userId, request);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader(xSharerUserId) long userId,
                                      @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@PathVariable Long bookingId, @RequestHeader(xSharerUserId) Long userId,
                                          @RequestParam(name = "approved") Boolean isApproved) {
        log.info("Approving booking {}, userId={}, approved={}", bookingId, userId, isApproved);
        return bookingClient.approveBooking(bookingId, userId, isApproved);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader(xSharerUserId) long userId,
                                                  @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Long from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Long size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new InternalServerException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader(xSharerUserId) Long userId,
                                                   @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Long from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Long size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new InternalServerException("Unknown state: " + stateParam));
        log.info("Get owner bookings with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getOwnerBookings(userId, state, from, size);
    }
}
