package java.ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Test
    public void findByBooker_IdAndEndBeforeOrderByStartDesc_WhenMultipleBookingsExist_ReturnsInDescendingOrder() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setItem(item);
        booking1.setStart(LocalDateTime.now().minusHours(2));
        booking1.setEnd(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now().minusHours(4));
        booking2.setEnd(LocalDateTime.now().minusHours(3));
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findByBooker_IdAndEndBeforeOrderByStartDesc(user.getId(), LocalDateTime.now(), Pageable.unpaged());

        assertEquals(2, bookings.size());
        assertTrue(bookings.get(0).getStart().isAfter(bookings.get(1).getStart()));
    }

    @Test
    public void findByBooker_IdAndEndBeforeOrderByStartDesc_WhenNoBookingExists_ReturnsEmptyList() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        List<Booking> bookings = bookingRepository.findByBooker_IdAndEndBeforeOrderByStartDesc(user.getId(), LocalDateTime.now(), Pageable.unpaged());

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findByBooker_IdOrderByStartDesc_WhenMultipleBookingsExist_ReturnsInDescendingOrder() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setItem(item);
        booking1.setStart(LocalDateTime.now().minusHours(2));
        booking1.setEnd(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now().minusHours(4));
        booking2.setEnd(LocalDateTime.now().minusHours(3));
        bookingRepository.save(booking2);

        Pageable pageable = Pageable.unpaged();
        List<Booking> bookings = bookingRepository.findByBooker_IdOrderByStartDesc(user.getId(), pageable);

        assertEquals(2, bookings.size());
        assertTrue(bookings.get(0).getStart().isAfter(bookings.get(1).getStart()));
    }

    @Test
    void findByBooker_IdOrderByStartDesc_WhenNoBookingExists_ReturnsEmptyList() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        Pageable pageable = Pageable.unpaged();
        List<Booking> bookings = bookingRepository.findByBooker_IdOrderByStartDesc(user.getId(), pageable);

        assertTrue(bookings.isEmpty());
    }


    @Test
    public void findByBooker_IdAndStatusOrderByStartDesc_WhenOneBookingWithStatus_ReturnsOneBooking() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(user.getId(), Status.APPROVED, Pageable.unpaged());

        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void findByBooker_IdAndEndIsAfterAndStartBeforeOrderByStartDesc_WhenMultipleBookingsExistWithinTimeRange_ReturnsInDescendingOrder() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setItem(item);
        booking1.setStart(LocalDateTime.now().minusHours(2));
        booking1.setEnd(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now().minusHours(4));
        booking2.setEnd(LocalDateTime.now().minusHours(3));
        bookingRepository.save(booking2);

        LocalDateTime startBefore = LocalDateTime.now().minusHours(1);
        LocalDateTime endAfter = LocalDateTime.now().minusHours(5);

        List<Booking> bookings = bookingRepository.findByBooker_IdAndEndIsAfterAndStartBeforeOrderByStartDesc(user.getId(), endAfter, startBefore, Pageable.unpaged());

        assertEquals(2, bookings.size());
        assertTrue(bookings.get(0).getStart().isAfter(bookings.get(1).getStart()));
    }


    @Test
    public void findByItem_Owner_IdOrderByStartDesc_WhenBookingsExist_ReturnsBookingsInDescendingOrder() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setItem(item);
        booking1.setStart(LocalDateTime.now().minusHours(2));
        booking1.setEnd(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now().minusHours(4));
        booking2.setEnd(LocalDateTime.now().minusHours(3));
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findByItem_Owner_IdOrderByStartDesc(user.getId(), Pageable.unpaged());

        assertEquals(2, bookings.size());
        assertTrue(bookings.get(0).getStart().isAfter(bookings.get(1).getStart()));
    }

    @Test
    public void findByItem_Owner_IdOrderByStartDesc_WhenPageSizeIsSpecified_ReturnsCorrectNumberOfBookings() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        for (int i = 0; i < 15; i++) {
            Booking booking = new Booking();
            booking.setBooker(user);
            booking.setItem(item);
            booking.setStart(LocalDateTime.now().minusHours(i));
            booking.setEnd(LocalDateTime.now().minusHours(i - 1));
            bookingRepository.save(booking);
        }

        Pageable pageable = PageRequest.of(0, 10); // Requesting first page with 10 bookings
        List<Booking> bookings = bookingRepository.findByItem_Owner_IdOrderByStartDesc(user.getId(), pageable);

        assertEquals(10, bookings.size());
    }

    @Test
    public void findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc_WhenNoBookingsBeforeSpecifiedDateTime_ReturnsEmptyList() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        LocalDateTime endDateTime = LocalDateTime.now().minusHours(1);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setItem(item);
        booking1.setStart(LocalDateTime.now());
        booking1.setEnd(LocalDateTime.now().plusHours(1));
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now().plusHours(2));
        booking2.setEnd(LocalDateTime.now().plusHours(3));
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(user.getId(), endDateTime, Pageable.unpaged());

        assertTrue(bookings.isEmpty());
    }

    @Test
    public void findByItem_Owner_IdAndEndAfterAndStartBeforeOrderByStartDesc_WhenBookingsWithinTimeRangeExist_ReturnsBookingsInDescendingOrder() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        LocalDateTime startDateTime = LocalDateTime.now().minusHours(3);
        LocalDateTime endDateTime = LocalDateTime.now().minusHours(1);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setItem(item);
        booking1.setStart(LocalDateTime.now().minusHours(2));
        booking1.setEnd(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now().minusHours(4));
        booking2.setEnd(LocalDateTime.now().minusHours(3));
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findByItem_Owner_IdAndEndAfterAndStartBeforeOrderByStartDesc(user.getId(), startDateTime, endDateTime, Pageable.unpaged());

        assertEquals(2, bookings.size());
        assertTrue(bookings.get(0).getStart().isAfter(bookings.get(1).getStart()));
    }

    @Test
    public void findByItem_Owner_IdAndStatus_WhenBookingsExistWithSpecifiedStatus_ReturnsMatchingBookings() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setItem(item);
        booking1.setStatus(Status.APPROVED);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setItem(item);
        booking2.setStatus(Status.WAITING);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findByItem_Owner_IdAndStatus(user.getId(), Status.APPROVED, Pageable.unpaged());

        assertEquals(1, bookings.size());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    public void findByItem_Owner_IdAndStatus_WhenPageSizeIsSpecified_ReturnsCorrectNumberOfBookings() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        for (int i = 0; i < 15; i++) {
            Booking booking = new Booking();
            booking.setBooker(user);
            booking.setItem(item);
            booking.setStatus(Status.APPROVED);
            bookingRepository.save(booking);
        }

        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingRepository.findByItem_Owner_IdAndStatus(user.getId(), Status.APPROVED, pageable);

        assertEquals(10, bookings.size());
    }

    @Test
    public void findByBooker_IdAndStartAfterOrderByStartDesc_WhenBookingsExistAfterSpecifiedDateTime_ReturnsBookingsInDescendingOrder() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        LocalDateTime startDateTime = LocalDateTime.now().minusHours(3);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setStart(LocalDateTime.now().minusHours(2));
        booking1.setEnd(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(LocalDateTime.now().minusHours(4));
        booking2.setEnd(LocalDateTime.now().minusHours(3));
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findByBooker_IdAndStartAfterOrderByStartDesc(user.getId(), startDateTime, Pageable.unpaged());

        assertEquals(1, bookings.size());
        assertEquals(booking1, bookings.get(0));
    }

    @Test
    public void findByBooker_IdAndStartAfterOrderByStartDesc_WhenNoBookingsAfterSpecifiedDateTime_ReturnsEmptyList() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        LocalDateTime startDateTime = LocalDateTime.now().plusHours(1);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setStart(LocalDateTime.now().minusHours(2));
        booking1.setEnd(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(LocalDateTime.now().minusHours(4));
        booking2.setEnd(LocalDateTime.now().minusHours(3));
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findByBooker_IdAndStartAfterOrderByStartDesc(user.getId(), startDateTime, Pageable.unpaged());

        assertTrue(bookings.isEmpty());
    }


    @Test
    public void findByItem_Owner_IdAndStartAfterOrderByStartDesc_WhenBookingsExistAfterSpecifiedDateTime_ReturnsBookingsInDescendingOrder() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        LocalDateTime startDateTime = LocalDateTime.now().minusHours(3);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setItem(item);
        booking1.setStart(LocalDateTime.now().minusHours(2));
        booking1.setEnd(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now().minusHours(4));
        booking2.setEnd(LocalDateTime.now().minusHours(3));
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findByItem_Owner_IdAndStartAfterOrderByStartDesc(user.getId(), startDateTime, Pageable.unpaged());

        assertEquals(1, bookings.size());
        assertEquals(booking1, bookings.get(0));
    }

    @Test
    public void findByItem_Owner_IdAndStartAfterOrderByStartDesc_WhenNoBookingsAfterSpecifiedDateTime_ReturnsEmptyList() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        LocalDateTime startDateTime = LocalDateTime.now().plusHours(1);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setItem(item);
        booking1.setStart(LocalDateTime.now().minusHours(2));
        booking1.setEnd(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now().minusHours(4));
        booking2.setEnd(LocalDateTime.now().minusHours(3));
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findByItem_Owner_IdAndStartAfterOrderByStartDesc(user.getId(), startDateTime, Pageable.unpaged());

        assertTrue(bookings.isEmpty());
    }

    @Test
    public void findFirstByItemIdAndStartBeforeOrderByStartDesc_WhenBookingExistsBeforeSpecifiedTime_ReturnsBookingWithLatestStartTime() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        LocalDateTime time = LocalDateTime.now();

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setItem(item);
        booking1.setStart(LocalDateTime.now().minusHours(2));
        booking1.setEnd(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now().minusHours(4));
        booking2.setEnd(LocalDateTime.now().minusHours(3));
        bookingRepository.save(booking2);

        Booking result = bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(), time);

        assertNotNull(result);
        assertEquals(booking1, result);
    }


    @Test
    public void findFirstByItemIdAndStartAfterOrderByStartAsc_WhenBookingExistsAfterSpecifiedTime_ReturnsBookingWithEarliestStartTime() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        LocalDateTime time = LocalDateTime.now().minusHours(1);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setItem(item);
        booking1.setStart(LocalDateTime.now().plusHours(2));
        booking1.setEnd(LocalDateTime.now().plusHours(3));
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now().plusHours(1));
        booking2.setEnd(LocalDateTime.now().plusHours(2));
        bookingRepository.save(booking2);

        Booking result = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), time);

        assertEquals(booking2, result);
    }

    @Test
    public void findFirstByItemIdAndStartAfterOrderByStartAsc_WhenNoBookingExistsAfterSpecifiedTime_ReturnsNull() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        LocalDateTime time = LocalDateTime.now().plusHours(3); // Specify time to filter bookings

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setItem(item);
        booking1.setStart(LocalDateTime.now().minusHours(2));
        booking1.setEnd(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setItem(item);
        booking2.setStart(LocalDateTime.now().minusHours(4));
        booking2.setEnd(LocalDateTime.now().minusHours(3));
        bookingRepository.save(booking2);

        Booking result = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), time);

        assertNull(result);
    }


    @Test
    public void existsBookingByItemId_WhenBookingExistsForItemId_ReturnsTrue() {
        // Setup
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking);

        boolean result = bookingRepository.existsBookingByItemId(item.getId());

        assertTrue(result);
    }

    @Test
    public void existsBookingByItemId_WhenNoBookingExistsForItemId_ReturnsFalse() {
        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        itemRepository.save(item);

        boolean result = bookingRepository.existsBookingByItemId(item.getId());

        assertFalse(result);
    }

    @Test
    public void existsBookingByBookerIdAndItemIdAndEndBefore_WhenBookingExists_ReturnsTrue() {
        // Setup
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(now.minusHours(2));
        booking.setEnd(now.minusHours(1));
        bookingRepository.save(booking);

        boolean result = bookingRepository.existsBookingByBookerIdAndItemIdAndEndBefore(user.getId(), item.getId(), now);

        assertTrue(result);
    }

    @Test
    public void existsBookingByBookerIdAndItemIdAndEndBefore_WhenNoBookingExists_ReturnsFalse() {
        // Setup
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();

        boolean result = bookingRepository.existsBookingByBookerIdAndItemIdAndEndBefore(user.getId(), item.getId(), now);

        assertFalse(result);
    }

}