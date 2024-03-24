package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingConverter;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final ru.practicum.shareit.booking.dao.bookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingConverter converter;

    @Override
    public BookingResponse create(BookingCreateRequest request, Long itemId, Long userId) {
        Booking booking = converter.convert(request);
        log.info("create booking = {}, itemId = {}, userId = {}", booking, itemId, userId);
        checkTime(booking);
        Item item = itemRepository.findById(itemId).orElseThrow(NoSuchElementException::new);
        checkItemAvailable(item);
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        checkOwnerNotBookingUser(booking, userId);
        return converter.convert(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse approve(Long bookingId, Long userId, Boolean isApproved) {
        log.info("approve bookingId = {}, userId = {}, isApproved = {}", bookingId, userId, isApproved);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(NoSuchElementException::new);
        checkItsOwner(booking, userId);
        if (isApproved) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new ValidationException("Status can not be changed after approved");
            }
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return converter.convert(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse get(Long bookingId, Long userId) {
        log.info("get booking, bookingId = {}, userId = {}", bookingId, userId);
        checkBookingExists(bookingId);
        checkUserExists(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(javax.validation.ValidationException::new);
        checkUserPermissionForBooking(booking, userId);
        return converter.convert(booking);
    }

    @Override
    public List<BookingResponse> getUserBookings(Long userId, String stateStr) {
        State state = strToState(stateStr);
        log.info("getUserBookings, userId = {}, state = {}", userId, state);
        checkUserExists(userId);
        switch (state) {
            case ALL:
                return converter.convert(bookingRepository
                        .findByBooker_IdOrderByStartDesc(userId));
            case PAST:
                return converter.convert(bookingRepository
                        .findByBooker_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()));
            case WAITING:
                return converter.convert(bookingRepository
                        .findByBooker_IdAndStatusOrderByStartDesc(userId, Status.WAITING));
            case REJECTED:
                return converter.convert(bookingRepository
                        .findByBooker_IdAndStatusOrderByStartDesc(userId, Status.REJECTED));
            case CURRENT:
                return converter.convert(bookingRepository
                        .findByBooker_IdAndEndIsAfterAndStartBeforeOrderByStartDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now()));
            case FUTURE:
                return converter.convert(bookingRepository
                        .findByBooker_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now()));
            default:
                return null;
        }
    }

    @Override
    public List<BookingResponse> getOwnerBookings(Long userId, String stateStr) {
        State state = strToState(stateStr);
        log.info("getOwnerBookings, userId = {}, state = {}", userId, state);
        checkUserExists(userId);
        switch (state) {
            case ALL:
                return converter.convert(bookingRepository.
                        findByItem_Owner_IdOrderByStartDesc(userId));
            case PAST:
                return converter.convert(bookingRepository
                        .findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now()));
            case CURRENT:
                return converter.convert(bookingRepository
                        .findByItem_Owner_IdAndEndAfterAndStartBeforeOrderByStartDesc(userId
                                , LocalDateTime.now(), LocalDateTime.now()));
            case WAITING:
                return converter.convert(bookingRepository
                        .findByItem_Owner_IdAndStatus(userId, Status.WAITING));
            case REJECTED:
                return converter.convert(bookingRepository
                        .findByItem_Owner_IdAndStatus(userId, Status.REJECTED));
            case FUTURE:
                return converter.convert(bookingRepository
                        .findByItem_Owner_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now()));
            default:
                return null;
        }
    }

    private void checkItemAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available for booking");
        }
    }

    private void checkItsOwner(Booking booking, Long ownerId) {
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("The item does not belong to this user");
        }
    }

    private void checkTime(Booking booking) {
        if (booking.getStart().isBefore(LocalDateTime.now()) || booking.getEnd().isBefore(LocalDateTime.now())
                || booking.getEnd().isBefore(booking.getStart()) || booking.getStart().isEqual(booking.getEnd())) {
            throw new ValidationException("Error in start and/or end time parameters");

        }
    }

    private State strToState(String str) {
        if (State.isValidValue(str)) return State.valueOf(str.toUpperCase());
        else throw new InternalServerException("Unknown state: UNSUPPORTED_STATUS");
    }

    private void checkBookingExists(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException("Booking doesn't exist");
        }
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User doesn't exist");
        }
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
