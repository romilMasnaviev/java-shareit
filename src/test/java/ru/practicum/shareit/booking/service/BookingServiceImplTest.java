package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingApproveResponse;
import ru.practicum.shareit.booking.dto.BookingConverter;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingGetResponse;
import ru.practicum.shareit.booking.model.Booking;
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
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingConverter converter;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    public void testCreate_BookingTimeInPast_ThrowsValidationException() {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setStart(LocalDateTime.now().minusHours(1));
        request.setEnd(LocalDateTime.now());

        Booking booking = new Booking();
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(converter.bookingCreateRequestConvertToBooking(request)).thenReturn(booking);

        assertThrows(ValidationException.class, () -> bookingService.create(request, 1L, 1L));
    }

    @Test
    public void testCreate_ItemNotAvailable_ThrowsValidationException() {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setStart(LocalDateTime.now().plusHours(1));
        request.setEnd(LocalDateTime.now().plusHours(2));

        Item item = new Item();
        item.setAvailable(false);

        Booking booking = new Booking();
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(converter.bookingCreateRequestConvertToBooking(request)).thenReturn(booking);

        assertThrows(ValidationException.class, () -> bookingService.create(request, 1L, 1L));
    }

    @Test
    public void testCreate_UserNotFound_NoSuchElementException() {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setStart(LocalDateTime.now().plusHours(1));
        request.setEnd(LocalDateTime.now().plusHours(2));

        Item item = new Item();
        item.setAvailable(true);

        Booking booking = new Booking();
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(itemRepository.findById(anyLong())).thenThrow(NoSuchElementException.class);
        when(converter.bookingCreateRequestConvertToBooking(request)).thenReturn(booking);

        assertThrows(NoSuchElementException.class, () -> bookingService.create(request, 1L, 1L));
    }


    @Test
    public void testCreate_UserBookingOwnItem_ThrowsNotFoundException() {
        User user = new User();
        user.setId(1L);

        BookingCreateRequest request = new BookingCreateRequest();
        request.setStart(LocalDateTime.now().plusHours(1));
        request.setEnd(LocalDateTime.now().plusHours(2));

        Item item = new Item();
        item.setOwner(user);
        item.setAvailable(true);

        Booking booking = new Booking();
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setItem(item);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(converter.bookingCreateRequestConvertToBooking(request)).thenReturn(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());

        assertThrows(NotFoundException.class, () -> bookingService.create(request, 1L, 1L));
    }

    @Test
    public void testApprove_BookingAlreadyApproved_ThrowsValidationException() {
        User user = new User();
        user.setId(1L);

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStatus(Status.APPROVED);

        Item item = new Item();
        item.setOwner(user);
        item.setAvailable(true);

        booking.setItem(item);

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.approve(1L, 1L, true));
    }

    @Test
    public void testApprove_ValidData_ReturnBookingApproveResponse() {
        User user = new User();
        user.setId(1L);

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);

        Item item = new Item();
        item.setOwner(user);
        item.setAvailable(true);

        booking.setItem(item);

        BookingApproveResponse expectedResponse = new BookingApproveResponse();
        expectedResponse.setId(1L);
        expectedResponse.setBooker(booking.getBooker());
        expectedResponse.setItem(booking.getItem());

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(converter.bookingConvertToBookingApproveResponse(booking)).thenReturn(expectedResponse);

        BookingApproveResponse actualResponse = bookingService.approve(1L, 1L, true);

        assertEquals(expectedResponse, actualResponse);
    }


    @Test
    public void testGet_BookingBelongsToUser_ReturnsBookingGetResponse() {
        long bookingId = 1L;
        long userId = 1L;

        User user = new User();
        user.setId(userId);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(user);

        Item item = new Item();
        item.setOwner(user);
        item.setAvailable(true);

        booking.setItem(item);

        BookingGetResponse expectedResponse = new BookingGetResponse();
        expectedResponse.setId(booking.getId());
        expectedResponse.setBooker(user);

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(converter.bookingConvertToBookingGetResponse(booking)).thenReturn(expectedResponse);

        BookingGetResponse actualResponse = bookingService.get(bookingId, userId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGet_BookingDoesNotBelongToUser_ThrowsPermissionDeniedException() {
        long bookingId = 1L;
        long userId = 1L;

        User user = new User();
        user.setId(userId);

        Booking booking = new Booking();
        booking.setId(bookingId);
        User newUser = new User();
        newUser.setId(2L);
        booking.setBooker(newUser);

        Item item = new Item();
        item.setOwner(newUser);
        item.setAvailable(true);

        booking.setItem(item);

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.get(bookingId, userId));
    }

    @Test
    public void testGetOwnerBookings_InvalidFromAndSize_ThrowValidationException() {
        assertThrows(ValidationException.class, () -> bookingService.getOwnerBookings(1L, "str", -1L, -1L));
    }

    @Test
    public void testGetOwnerBookings_InvalidStr_ThrowInternalServerException() {
        assertThrows(InternalServerException.class, () -> bookingService.getOwnerBookings(1L, "invalidStr", 1L, 1L));
    }

    @Test
    public void testGetUserBookings_InvalidFromAndSize_ThrowValidationException() {
        assertThrows(ValidationException.class, () -> bookingService.getUserBookings(1L, "str", -1L, -1L));
    }

    @Test
    public void testGetUserBookings_InvalidStr_ThrowInternalServerException() {
        assertThrows(InternalServerException.class, () -> bookingService.getUserBookings(1L, "invalidStr", 1L, 1L));
    }

}