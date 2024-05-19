package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId, Pageable pageable);

    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long bookerId, Status status, Pageable pageable);

    List<Booking> findByBooker_IdAndEndIsAfterAndStartBeforeOrderByStartDesc(Long bookerId, LocalDateTime endAfter, LocalDateTime startBefore, Pageable pageable);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndEndAfterAndStartBeforeOrderByStartDesc(Long userId, LocalDateTime localDateTime, LocalDateTime localDateTime1, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStatus(Long userId, Status status, Pageable pageable);

    List<Booking> findByBooker_IdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime localDateTime, Pageable pageable);

    Booking findFirstByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime time);

    Booking findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime time);

    Boolean existsBookingByItemId(Long itemId);

    Boolean existsBookingByBookerIdAndItemIdAndEndBefore(Long userId, Long itemId, LocalDateTime now);
}
