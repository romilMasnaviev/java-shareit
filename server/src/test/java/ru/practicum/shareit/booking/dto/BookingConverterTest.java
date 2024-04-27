package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class BookingConverterTest {

    @Autowired
    BookingConverter converter;

    @Test
    public void testBookingConvertToBookingResponse_WithNonNullBooker_BookingResponse() {
        Booking booking = new Booking();
        User user = new User();
        user.setId(1L);
        booking.setBooker(user);

        BookingResponse response = converter.bookingConvertToBookingResponse(booking);
        assertEquals(user.getId(), response.getBookerId());
    }

    @Test
    public void testBookingCreateRequestConvertToBooking_ValidRequest_ReturnsBooking() {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().plusHours(1));

        Booking booking = converter.bookingCreateRequestConvertToBooking(request);

        assertNotNull(booking);
        assertEquals(request.getStart(), booking.getStart());
        assertEquals(request.getEnd(), booking.getEnd());
    }

    @Test
    public void testBookingConvertToBookingCreateResponse_WithNonNullFields_ReturnsBookingCreateResponse() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(1));

        Item item = new Item();
        item.setId(1L);
        User user = new User();
        user.setId(1L);

        booking.setItem(item);
        booking.setBooker(user);

        BookingCreateResponse response = converter.bookingConvertToBookingCreateResponse(booking);

        assertNotNull(response);

        assertEquals(booking.getId(), response.getId());
        assertEquals(booking.getStart(), response.getStart());
        assertEquals(booking.getEnd(), response.getEnd());
        assertEquals(booking.getItem(), response.getItem());
        assertEquals(booking.getBooker(), response.getBooker());
    }

    @Test
    public void testBookingConvertToBookingApproveResponse_WithNonNullFields_ReturnsBookingApproveResponse() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(1));

        Item item = new Item();
        item.setId(1L);
        User user = new User();
        user.setId(1L);

        booking.setItem(item);
        booking.setBooker(user);

        BookingApproveResponse response = converter.bookingConvertToBookingApproveResponse(booking);

        assertNotNull(response);

        assertEquals(booking.getId(), response.getId());
        assertEquals(booking.getStart(), response.getStart());
        assertEquals(booking.getEnd(), response.getEnd());
        assertEquals(booking.getItem(), response.getItem());
        assertEquals(booking.getBooker(), response.getBooker());
    }

    @Test
    public void testBookingConvertToBookingGetResponse_WithNonNullFields_ReturnsBookingGetResponse() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(1));

        Item item = new Item();
        item.setId(1L);
        User user = new User();
        user.setId(1L);

        booking.setItem(item);
        booking.setBooker(user);

        BookingGetResponse response = converter.bookingConvertToBookingGetResponse(booking);

        assertNotNull(response);

        assertEquals(booking.getId(), response.getId());
        assertEquals(booking.getStart(), response.getStart());
        assertEquals(booking.getEnd(), response.getEnd());
        assertEquals(booking.getItem(), response.getItem());
        assertEquals(booking.getBooker(), response.getBooker());
    }

    @Test
    public void testBookingConvertToBookingGetResponseList_WithNonNullFields_ReturnsListBookingGetResponse() {
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStart(LocalDateTime.now());
        booking1.setEnd(LocalDateTime.now().plusHours(1));

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStart(LocalDateTime.now().plusDays(1));
        booking2.setEnd(LocalDateTime.now().plusDays(1).plusHours(1));

        List<Booking> bookings = Arrays.asList(booking1, booking2);

        Item item = new Item();
        item.setId(1L);
        User user = new User();
        user.setId(1L);

        booking1.setItem(item);
        booking1.setBooker(user);
        booking2.setItem(item);
        booking2.setBooker(user);

        List<BookingGetResponse> responses = converter.bookingConvertToBookingGetResponse(bookings);

        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals(booking1.getId(), responses.get(0).getId());
        assertEquals(booking1.getStart(), responses.get(0).getStart());
        assertEquals(booking1.getEnd(), responses.get(0).getEnd());
        assertEquals(booking1.getItem(), responses.get(0).getItem());
        assertEquals(booking1.getBooker(), responses.get(0).getBooker());

        assertEquals(booking2.getId(), responses.get(1).getId());
        assertEquals(booking2.getStart(), responses.get(1).getStart());
        assertEquals(booking2.getEnd(), responses.get(1).getEnd());
        assertEquals(booking2.getItem(), responses.get(1).getItem());
        assertEquals(booking2.getBooker(), responses.get(1).getBooker());
    }
}



