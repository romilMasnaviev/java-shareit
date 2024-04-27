package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
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
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    public void testGetOwnerBookings_AllState_ReturnBookings() {
        Long ownerId = 1L;
        String stateStr = "ALL";
        Long from = 0L;
        Long size = 10L;
        List<Booking> allBookings = Arrays.asList(new Booking(), new Booking());
        List<BookingGetResponse> expectedAllResponses = Arrays.asList(new BookingGetResponse(), new BookingGetResponse());

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(bookingRepository.findByItem_Owner_IdOrderByStartDesc(eq(ownerId), any(Pageable.class))).thenReturn(allBookings);
        when(converter.bookingConvertToBookingGetResponse(allBookings)).thenReturn(expectedAllResponses);

        List<BookingGetResponse> actualAllResponses = bookingService.getOwnerBookings(ownerId, stateStr, from, size);

        assertEquals(expectedAllResponses, actualAllResponses);
        verify(userService).checkUserDoesntExistAndThrowIfNotFound(ownerId);
        verify(bookingRepository).findByItem_Owner_IdOrderByStartDesc(eq(ownerId), any(Pageable.class));
        verify(converter).bookingConvertToBookingGetResponse(allBookings);
    }

    @Test
    public void testGetOwnerBookings_PastState_ReturnPastBookings() {
        Long ownerId = 1L;
        String stateStr = "PAST";
        Long from = 0L;
        Long size = 10L;
        List<Booking> pastBookings = Arrays.asList(new Booking(), new Booking());
        List<BookingGetResponse> expectedPastResponses = Arrays.asList(new BookingGetResponse(), new BookingGetResponse());

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(bookingRepository.findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(eq(ownerId), any(LocalDateTime.class), any(Pageable.class))).thenReturn(pastBookings);
        when(converter.bookingConvertToBookingGetResponse(pastBookings)).thenReturn(expectedPastResponses);

        List<BookingGetResponse> actualPastResponses = bookingService.getOwnerBookings(ownerId, stateStr, from, size);

        assertEquals(expectedPastResponses, actualPastResponses);
        verify(userService).checkUserDoesntExistAndThrowIfNotFound(ownerId);
        verify(bookingRepository).findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(eq(ownerId), any(LocalDateTime.class), any(Pageable.class));
        verify(converter).bookingConvertToBookingGetResponse(pastBookings);
    }

    @Test
    public void testGetOwnerBookings_CurrentState_ReturnCurrentBookings() {
        Long ownerId = 1L;
        String stateStr = "CURRENT";
        Long from = 0L;
        Long size = 10L;
        List<Booking> currentBookings = Arrays.asList(new Booking(), new Booking());
        List<BookingGetResponse> expectedCurrentResponses = Arrays.asList(new BookingGetResponse(), new BookingGetResponse());

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(bookingRepository.findByItem_Owner_IdAndEndAfterAndStartBeforeOrderByStartDesc(eq(ownerId), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(currentBookings);
        when(converter.bookingConvertToBookingGetResponse(currentBookings)).thenReturn(expectedCurrentResponses);

        List<BookingGetResponse> actualCurrentResponses = bookingService.getOwnerBookings(ownerId, stateStr, from, size);

        assertEquals(expectedCurrentResponses, actualCurrentResponses);
        verify(userService).checkUserDoesntExistAndThrowIfNotFound(ownerId);
        verify(bookingRepository).findByItem_Owner_IdAndEndAfterAndStartBeforeOrderByStartDesc(eq(ownerId), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(converter).bookingConvertToBookingGetResponse(currentBookings);
    }

    @Test
    public void testGetOwnerBookings_WaitingState_ReturnWaitingBookings() {
        Long ownerId = 1L;
        String stateStr = "WAITING";
        Long from = 0L;
        Long size = 10L;
        List<Booking> waitingBookings = Arrays.asList(new Booking(), new Booking());
        List<BookingGetResponse> expectedWaitingResponses = Arrays.asList(new BookingGetResponse(), new BookingGetResponse());

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(bookingRepository.findByItem_Owner_IdAndStatus(eq(ownerId), eq(Status.WAITING), any(Pageable.class))).thenReturn(waitingBookings);
        when(converter.bookingConvertToBookingGetResponse(waitingBookings)).thenReturn(expectedWaitingResponses);

        List<BookingGetResponse> actualWaitingResponses = bookingService.getOwnerBookings(ownerId, stateStr, from, size);

        assertEquals(expectedWaitingResponses, actualWaitingResponses);
        verify(userService).checkUserDoesntExistAndThrowIfNotFound(ownerId);
        verify(bookingRepository).findByItem_Owner_IdAndStatus(eq(ownerId), eq(Status.WAITING), any(Pageable.class));
        verify(converter).bookingConvertToBookingGetResponse(waitingBookings);
    }

    @Test
    public void testGetOwnerBookings_RejectedState_ReturnRejectedBookings() {
        Long ownerId = 1L;
        String stateStr = "REJECTED";
        Long from = 0L;
        Long size = 10L;
        List<Booking> rejectedBookings = Arrays.asList(new Booking(), new Booking());
        List<BookingGetResponse> expectedRejectedResponses = Arrays.asList(new BookingGetResponse(), new BookingGetResponse());

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(bookingRepository.findByItem_Owner_IdAndStatus(eq(ownerId), eq(Status.REJECTED), any(Pageable.class))).thenReturn(rejectedBookings);
        when(converter.bookingConvertToBookingGetResponse(rejectedBookings)).thenReturn(expectedRejectedResponses);

        List<BookingGetResponse> actualRejectedResponses = bookingService.getOwnerBookings(ownerId, stateStr, from, size);

        assertEquals(expectedRejectedResponses, actualRejectedResponses);
        verify(userService).checkUserDoesntExistAndThrowIfNotFound(ownerId);
        verify(bookingRepository).findByItem_Owner_IdAndStatus(eq(ownerId), eq(Status.REJECTED), any(Pageable.class));
        verify(converter).bookingConvertToBookingGetResponse(rejectedBookings);
    }

    @Test
    public void testGetOwnerBookings_FutureState_ReturnFutureBookings() {
        Long ownerId = 1L;
        String stateStr = "FUTURE";
        Long from = 0L;
        Long size = 10L;
        List<Booking> futureBookings = Arrays.asList(new Booking(), new Booking());
        List<BookingGetResponse> expectedFutureResponses = Arrays.asList(new BookingGetResponse(), new BookingGetResponse());

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(bookingRepository.findByItem_Owner_IdAndStartAfterOrderByStartDesc(eq(ownerId), any(LocalDateTime.class), any(Pageable.class))).thenReturn(futureBookings);
        when(converter.bookingConvertToBookingGetResponse(futureBookings)).thenReturn(expectedFutureResponses);

        List<BookingGetResponse> actualFutureResponses = bookingService.getOwnerBookings(ownerId, stateStr, from, size);

        assertEquals(expectedFutureResponses, actualFutureResponses);
        verify(userService).checkUserDoesntExistAndThrowIfNotFound(ownerId);
        verify(bookingRepository).findByItem_Owner_IdAndStartAfterOrderByStartDesc(eq(ownerId), any(LocalDateTime.class), any(Pageable.class));
        verify(converter).bookingConvertToBookingGetResponse(futureBookings);
    }

    @Test
    public void testGetUserBookings_InvalidFromAndSize_ThrowValidationException() {
        assertThrows(ValidationException.class, () -> bookingService.getUserBookings(1L, "str", -1L, -1L));
    }

    @Test
    public void testGetUserBookings_InvalidStr_ThrowInternalServerException() {
        assertThrows(InternalServerException.class, () -> bookingService.getUserBookings(1L, "invalidStr", 1L, 1L));
    }

    @Test
    public void testGetUserBookings_AllState_ReturnBookings() {
        Long userId = 1L;
        String stateStr = "ALL";
        Long from = 0L;
        Long size = 10L;
        List<Booking> bookings = Arrays.asList(new Booking(), new Booking());
        List<BookingGetResponse> expectedResponses = Arrays.asList(new BookingGetResponse(), new BookingGetResponse());

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(bookingRepository.findByBooker_IdOrderByStartDesc(eq(userId), any(Pageable.class))).thenReturn(bookings);
        when(converter.bookingConvertToBookingGetResponse(bookings)).thenReturn(expectedResponses);

        List<BookingGetResponse> actualResponses = bookingService.getUserBookings(userId, stateStr, from, size);

        assertEquals(expectedResponses, actualResponses);
        verify(userService).checkUserDoesntExistAndThrowIfNotFound(userId);
        verify(bookingRepository).findByBooker_IdOrderByStartDesc(eq(userId), any(Pageable.class));
        verify(converter).bookingConvertToBookingGetResponse(bookings);
    }

    @Test
    public void testGetUserBookings_PastState_ReturnBookings() {
        Long userId = 1L;
        String stateStr = "PAST";
        Long from = 0L;
        Long size = 10L;
        List<Booking> pastBookings = Arrays.asList(new Booking(), new Booking());
        List<BookingGetResponse> expectedPastResponses = Arrays.asList(new BookingGetResponse(), new BookingGetResponse());

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(bookingRepository.findByBooker_IdAndEndBeforeOrderByStartDesc(eq(userId), any(LocalDateTime.class), any(Pageable.class))).thenReturn(pastBookings);
        when(converter.bookingConvertToBookingGetResponse(pastBookings)).thenReturn(expectedPastResponses);

        List<BookingGetResponse> actualPastResponses = bookingService.getUserBookings(userId, stateStr, from, size);

        assertEquals(expectedPastResponses, actualPastResponses);
        verify(userService).checkUserDoesntExistAndThrowIfNotFound(userId);
        verify(bookingRepository).findByBooker_IdAndEndBeforeOrderByStartDesc(eq(userId), any(LocalDateTime.class), any(Pageable.class));
        verify(converter).bookingConvertToBookingGetResponse(pastBookings);
    }

    @Test
    public void testGetUserBookings_WaitingState_ReturnBookings() {
        Long userId = 1L;
        String stateStr = "WAITING";
        Long from = 0L;
        Long size = 10L;
        List<Booking> waitingBookings = Arrays.asList(new Booking(), new Booking());
        List<BookingGetResponse> expectedWaitingResponses = Arrays.asList(new BookingGetResponse(), new BookingGetResponse());

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(eq(userId), eq(Status.WAITING), any(Pageable.class))).thenReturn(waitingBookings);
        when(converter.bookingConvertToBookingGetResponse(waitingBookings)).thenReturn(expectedWaitingResponses);

        List<BookingGetResponse> actualWaitingResponses = bookingService.getUserBookings(userId, stateStr, from, size);

        assertEquals(expectedWaitingResponses, actualWaitingResponses);
        verify(userService).checkUserDoesntExistAndThrowIfNotFound(userId);
        verify(bookingRepository).findByBooker_IdAndStatusOrderByStartDesc(eq(userId), eq(Status.WAITING), any(Pageable.class));
        verify(converter).bookingConvertToBookingGetResponse(waitingBookings);
    }

    @Test
    public void testGetUserBookings_RejectedState_ReturnBookings() {
        Long userId = 1L;
        String stateStr = "REJECTED";
        Long from = 0L;
        Long size = 10L;
        List<Booking> rejectedBookings = Arrays.asList(new Booking(), new Booking());
        List<BookingGetResponse> expectedRejectedResponses = Arrays.asList(new BookingGetResponse(), new BookingGetResponse());

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(eq(userId), eq(Status.REJECTED), any(Pageable.class))).thenReturn(rejectedBookings);
        when(converter.bookingConvertToBookingGetResponse(rejectedBookings)).thenReturn(expectedRejectedResponses);

        List<BookingGetResponse> actualRejectedResponses = bookingService.getUserBookings(userId, stateStr, from, size);

        assertEquals(expectedRejectedResponses, actualRejectedResponses);
        verify(userService).checkUserDoesntExistAndThrowIfNotFound(userId);
        verify(bookingRepository).findByBooker_IdAndStatusOrderByStartDesc(eq(userId), eq(Status.REJECTED), any(Pageable.class));
        verify(converter).bookingConvertToBookingGetResponse(rejectedBookings);
    }

    @Test
    public void testGetUserBookings_CurrentState_ReturnBookings() {
        Long userId = 1L;
        String stateStr = "CURRENT";
        Long from = 0L;
        Long size = 10L;
        List<Booking> currentBookings = Arrays.asList(new Booking(), new Booking());
        List<BookingGetResponse> expectedCurrentResponses = Arrays.asList(new BookingGetResponse(), new BookingGetResponse());

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(bookingRepository.findByBooker_IdAndEndIsAfterAndStartBeforeOrderByStartDesc(eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(currentBookings);
        when(converter.bookingConvertToBookingGetResponse(currentBookings)).thenReturn(expectedCurrentResponses);

        List<BookingGetResponse> actualCurrentResponses = bookingService.getUserBookings(userId, stateStr, from, size);

        assertEquals(expectedCurrentResponses, actualCurrentResponses);
        verify(userService).checkUserDoesntExistAndThrowIfNotFound(userId);
        verify(bookingRepository).findByBooker_IdAndEndIsAfterAndStartBeforeOrderByStartDesc(eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(converter).bookingConvertToBookingGetResponse(currentBookings);
    }

    @Test
    public void testGetUserBookings_FutureState_ReturnBookings() {
        Long userId = 1L;
        String stateStr = "FUTURE";
        Long from = 0L;
        Long size = 10L;
        List<Booking> futureBookings = Arrays.asList(new Booking(), new Booking());
        List<BookingGetResponse> expectedFutureResponses = Arrays.asList(new BookingGetResponse(), new BookingGetResponse());

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(bookingRepository.findByBooker_IdAndStartAfterOrderByStartDesc(eq(userId), any(LocalDateTime.class), any(Pageable.class))).thenReturn(futureBookings);
        when(converter.bookingConvertToBookingGetResponse(futureBookings)).thenReturn(expectedFutureResponses);

        List<BookingGetResponse> actualFutureResponses = bookingService.getUserBookings(userId, stateStr, from, size);

        assertEquals(expectedFutureResponses, actualFutureResponses);
        verify(userService).checkUserDoesntExistAndThrowIfNotFound(userId);
        verify(bookingRepository).findByBooker_IdAndStartAfterOrderByStartDesc(eq(userId), any(LocalDateTime.class), any(Pageable.class));
        verify(converter).bookingConvertToBookingGetResponse(futureBookings);
    }


}