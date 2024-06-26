package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.handler.InternalServerException;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.handler.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static ru.practicum.shareit.utility.PaginationUtil.getPageable;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingConverter converter;

    private final UserService userService;

    @Override
    public BookingCreateResponse create(BookingCreateRequest request, Long itemId, Long userId) {
        log.info("Creating booking. Request: {}, Item ID: {}, User ID: {}", request, itemId, userId);
        userService.checkUserDoesntExistAndThrowIfNotFound(userId);
        Booking booking = converter.bookingCreateRequestConvertToBooking(request);
        checkTime(booking);

        Item item = getItem(itemId);
        checkItemAvailable(item);
        User user = getUser(userId);

        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        checkOwnerNotBookingUser(booking, userId);
        return converter.bookingConvertToBookingCreateResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingApproveResponse approve(Long bookingId, Long userId, Boolean isApproved) {
        log.info("Approving booking. Booking ID: {}, User ID: {}, Approval: {}", bookingId, userId, isApproved);
        userService.checkUserDoesntExistAndThrowIfNotFound(userId);
        Booking booking = getBooking(bookingId);
        checkItsOwner(booking, userId);
        if (isApproved) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new ValidationException("Status cannot be changed after approval");
            }
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return converter.bookingConvertToBookingApproveResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingGetResponse get(Long bookingId, Long userId) {
        log.info("Fetching booking. Booking ID: {}, User ID: {}", bookingId, userId);
        userService.checkUserDoesntExistAndThrowIfNotFound(userId);
        Booking booking = getBooking(bookingId);
        checkUserPermissionForBooking(booking, userId);
        return converter.bookingConvertToBookingGetResponse(booking);
    }

    @Override
    public List<BookingGetResponse> getOwnerBookings(Long userId, String stateStr, Long from, Long size) {
        Pageable pageable = getPageable(from, size);
        State state = strToState(stateStr);
        log.info("Fetching owner bookings. User ID: {}, State: {}", userId, state);
        userService.checkUserDoesntExistAndThrowIfNotFound(userId);
        switch (state) {
            case ALL:
                return converter.bookingConvertToBookingGetResponse(bookingRepository
                        .findByItem_Owner_IdOrderByStartDesc(userId, pageable));
            case PAST:
                return converter.bookingConvertToBookingGetResponse(bookingRepository
                        .findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable));
            case CURRENT:
                return converter.bookingConvertToBookingGetResponse(bookingRepository
                        .findByItem_Owner_IdAndEndAfterAndStartBeforeOrderByStartDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now(), pageable));
            case WAITING:
                return converter.bookingConvertToBookingGetResponse(bookingRepository
                        .findByItem_Owner_IdAndStatus(userId, Status.WAITING, pageable));
            case REJECTED:
                return converter.bookingConvertToBookingGetResponse(bookingRepository
                        .findByItem_Owner_IdAndStatus(userId, Status.REJECTED, pageable));
            case FUTURE:
                return converter.bookingConvertToBookingGetResponse(bookingRepository
                        .findByItem_Owner_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable));
            default:
                return null;
        }
    }

    @Override
    public List<BookingGetResponse> getUserBookings(Long userId, String stateStr, Long from, Long size) {
        Pageable pageable = getPageable(from, size);
        State state = strToState(stateStr);
        log.info("Fetching user bookings. User ID: {}, State: {}", userId, state);
        userService.checkUserDoesntExistAndThrowIfNotFound(userId);
        switch (state) {
            case ALL:
                return converter.bookingConvertToBookingGetResponse(bookingRepository
                        .findByBooker_IdOrderByStartDesc(userId, pageable));
            case PAST:
                return converter.bookingConvertToBookingGetResponse(bookingRepository
                        .findByBooker_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable));
            case WAITING:
                return converter.bookingConvertToBookingGetResponse(bookingRepository
                        .findByBooker_IdAndStatusOrderByStartDesc(userId, Status.WAITING, pageable));
            case REJECTED:
                return converter.bookingConvertToBookingGetResponse(bookingRepository
                        .findByBooker_IdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageable));
            case CURRENT:
                return converter.bookingConvertToBookingGetResponse(bookingRepository
                        .findByBooker_IdAndEndIsAfterAndStartBeforeOrderByStartDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now(), pageable));
            case FUTURE:
                return converter.bookingConvertToBookingGetResponse(bookingRepository
                        .findByBooker_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable));
            default:
                return null;
        }
    }

    private void checkItemAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new ValidationException("This item is not available for booking");
        }
    }

    private void checkItsOwner(Booking booking, Long ownerId) {
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("This item does not belong to this user");
        }
    }

    private void checkTime(Booking booking) {
        if (booking.getStart().isBefore(LocalDateTime.now()) || booking.getEnd().isBefore(LocalDateTime.now())
                || booking.getEnd().isBefore(booking.getStart()) || booking.getStart().isEqual(booking.getEnd())) {
            throw new ValidationException("Error in start and/or end time parameters");
        }
    }

    private State strToState(String str) {
        if (State.isValidValue(str)) {
            return State.valueOf(str.toUpperCase());
        } else {
            throw new InternalServerException("Unknown state: " + str);
        }
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NoSuchElementException("Booking not found"));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NoSuchElementException("Item not found"));
    }

    private void checkUserPermissionForBooking(Booking booking, Long userId) {
        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new NotFoundException("User does not have permission for this booking");
        }
    }

    private void checkOwnerNotBookingUser(Booking booking, Long userId) {
        if (booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("You cannot be both the owner and the creator of a booking.");
        }
    }

}
