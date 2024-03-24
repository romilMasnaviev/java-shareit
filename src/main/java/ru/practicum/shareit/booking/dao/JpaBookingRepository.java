package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaBookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findByBooker_IdAndEndIsAfterAndStartBeforeOrderByStartDesc(Long bookerId, LocalDateTime endAfter, LocalDateTime startBefore);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long userId);

    List<Booking> findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime localDateTime);

    List<Booking> findByItem_Owner_IdAndEndAfterAndStartBeforeOrderByStartDesc(Long userId, LocalDateTime localDateTime, LocalDateTime localDateTime1);

    List<Booking> findByItem_Owner_IdAndStatus(Long userId, Status status);

    List<Booking> findByBooker_IdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime localDateTime);

    List<Booking> findByItem_Owner_IdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime localDateTime);

    Booking findFirstByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime time);

    Booking findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime time);

    Boolean existsBookingByItemId(Long itemId);


    Boolean existsBookingByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime localDateTime);

}
