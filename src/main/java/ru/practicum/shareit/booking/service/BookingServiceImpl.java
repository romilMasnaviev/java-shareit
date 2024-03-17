package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.JpaBookingRepository;
import ru.practicum.shareit.booking.dto.BookingApproveResponse;
import ru.practicum.shareit.booking.dto.BookingConverter;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.handler.ValidationException;
import ru.practicum.shareit.item.dao.JpaItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.JpaUserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final JpaBookingRepository bookingRepository;
    private final JpaItemRepository itemRepository;
    private final JpaUserRepository userRepository;
    private final BookingConverter converter;

    @Override
    public Booking create(Booking booking, Long itemId, Long userId) {
        log.info("create booking = {}, itemId = {}, userId = {}", booking, itemId, userId);
        checkTime(booking);
        Item item = itemRepository.findById(itemId).orElseThrow(NoSuchElementException::new);
        checkItemAvailable(item);
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    public BookingApproveResponse approve(Long bookingId, Long userId, Boolean isApproved) {
        log.info("approve bookingId = {}, userId = {}, isApproved = {}", bookingId,userId,isApproved);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(NoSuchElementException::new);
        checkItsOwner(booking, userId);
        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return converter.convert(bookingRepository.save(booking));
    }

    private void checkItemAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для брони");
        }
    }

    private void checkItsOwner(Booking booking, Long ownerId) {
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ValidationException("Вещь не принадлежит этому пользователю");
        }
    }

    private void checkTime(Booking booking) {
        if (booking.getStart().isBefore(LocalDateTime.now()) ||
                booking.getEnd().isBefore(LocalDateTime.now()) ||
                booking.getEnd().isBefore(booking.getStart()) ||
                booking.getStart().equals(booking.getEnd())) {
            throw new ValidationException("Ошибка времени страта и/или времени конца показателей");
        }
    }

}
