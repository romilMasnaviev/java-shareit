package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingConverter;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.handler.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.itemRequest.dao.ItemRequestRepository;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {


    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemConverter itemConverter;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentConverter commentConverter;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingConverter bookingConverter;

    @Test
    public void testCreate_UserDoesntExists_NotFoundException() {
        long ownerId = 1L;
        ItemCreateRequest request = new ItemCreateRequest();
        request.setName("name");
        request.setAvailable(true);
        request.setDescription("description");

        Item item = new Item();
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());
        item.setName(request.getName());

        when(itemConverter.itemCreateRequestConvertToItem(request)).thenReturn(item);
        doThrow(new EntityNotFoundException("User with ID " + ownerId + " not found")).when(userRepository).findById(anyLong());

        assertThrows(EntityNotFoundException.class, () -> itemService.create(request, ownerId));
    }

    @Test
    public void testCreate_InvalidItemRequest_ThrowValidationException() {
        long ownerId = 1L;
        ItemCreateRequest request = new ItemCreateRequest();
        request.setName("name");
        request.setAvailable(true);
        request.setDescription("description");
        request.setRequestId(1L);

        Item item = new Item();
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());
        item.setName(request.getName());

        User user = new User();
        user.setId(ownerId);
        user.setName("name");
        user.setEmail("mail@mail.ru");

        when(itemConverter.itemCreateRequestConvertToItem(request)).thenReturn(item);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenThrow(ValidationException.class);

        assertThrows(ValidationException.class, () -> itemService.create(request, ownerId));
    }

    @Test
    public void testCreate_SuccessfulCreation_ReturnItemCreateResponse() {
        long ownerId = 1L;
        ItemCreateRequest request = new ItemCreateRequest();
        request.setName("name");
        request.setAvailable(true);
        request.setDescription("description");
        request.setRequestId(1L);

        Item item = new Item();
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());
        item.setName(request.getName());

        User user = new User();
        user.setId(ownerId);
        user.setName("name");
        user.setEmail("mail@mail.ru");

        ItemCreateResponse response = new ItemCreateResponse();
        response.setId(1L);
        response.setName(request.getName());
        response.setDescription(request.getDescription());
        response.setAvailable(request.getAvailable());
        response.setRequestId(request.getRequestId());

        when(itemConverter.itemCreateRequestConvertToItem(request)).thenReturn(item);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(new ItemRequest()));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemConverter.itemConvertToItemCreateResponse(any(Item.class))).thenReturn(response);

        ItemCreateResponse actualResponse = itemService.create(request, ownerId);

        assertEquals(response, actualResponse);
    }

    @Test
    public void testUpdate_UserDoesntExists_NotFoundException() {
        long ownerId = 1L;
        ItemUpdateRequest request = new ItemUpdateRequest();
        request.setName("name");
        request.setAvailable(true);
        request.setDescription("description");

        doThrow(NotFoundException.class).when(userService).checkUserDoesntExistAndThrowIfNotFound(anyLong());

        assertThrows(NotFoundException.class, () -> itemService.update(request, ownerId, 1L));
    }

    @Test
    public void testUpdate_ValidData_ReturnItemUpdateResponse() {
        long ownerId = 1L;
        long itemId = 1L;
        ItemUpdateRequest request = new ItemUpdateRequest();
        request.setName("name");
        request.setAvailable(true);
        request.setDescription("description");

        User user = new User();
        user.setId(ownerId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(user);

        ItemUpdateResponse expectedResponse = new ItemUpdateResponse();
        expectedResponse.setId(1L);
        expectedResponse.setName(request.getName());
        expectedResponse.setAvailable(request.getAvailable());
        expectedResponse.setDescription(request.getDescription());

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(anyLong());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemConverter.itemUpdateRequestConvertToItem(request)).thenReturn(item);
        when(itemConverter.itemConvertToItemUpdateResponse(itemRepository.save(any()))).thenReturn(expectedResponse);

        assertEquals(expectedResponse, itemService.update(request, ownerId, itemId));
    }

    @Test
    public void testGetAll_UserDoesntExist_ThrowsEntityNotFoundException() {
        long ownerId = 1L;
        long from = 0L;
        long size = 10L;

        doThrow(EntityNotFoundException.class).when(userService).checkUserDoesntExistAndThrowIfNotFound(ownerId);

        assertThrows(EntityNotFoundException.class, () -> itemService.getAll(ownerId, from, size));
    }

    @Test
    public void testSearch_UserDoesntExist_ThrowsEntityNotFoundException() {
        long userId = 1L;
        String keyword = "search keyword";
        long from = 0L;
        long size = 10L;

        doThrow(EntityNotFoundException.class).when(userService).checkUserDoesntExistAndThrowIfNotFound(userId);

        assertThrows(EntityNotFoundException.class, () -> itemService.search(userId, keyword, from, size));
    }

    @Test
    public void testGet_ItemBelongsToUserAndHasBooking_ReturnsItemGetResponseWithBooking() {
        long itemId = 1L;
        long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setName("name");
        user.setEmail("email@mail.ru");

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(user);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setAuthor(user);
        comment.setText("Comment content");

        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(comment.getId());
        commentResponse.setAuthorName(user.getName());
        commentResponse.setText(comment.getText());

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(1));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        when(itemRepository.existsItemByOwnerIdAndId(userId, itemId)).thenReturn(true);
        when(bookingRepository.existsBookingByItemId(itemId)).thenReturn(true);

        ItemGetResponse expectedResponse = new ItemGetResponse();
        expectedResponse.setId(item.getId());
        expectedResponse.setName("Item Name");
        expectedResponse.setDescription("Item Description");
        expectedResponse.setAvailable(true);
        when(itemConverter.itemConvertToItemGetResponse(item)).thenReturn(expectedResponse);

        when(commentConverter.convert(anyList())).thenReturn(List.of(commentResponse));
        ItemGetResponse response = itemService.get(itemId, userId);

        assertNotNull(response.getComments());
    }

    @Test
    public void testGet_ItemDoesNotBelongToUserOrHasNoBooking_ReturnsItemGetResponseWithoutBooking() {
        long itemId = 1L;
        long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setName("name");
        user.setEmail("email@mail.ru");

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(user);

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(userId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        when(itemRepository.existsItemByOwnerIdAndId(userId, itemId)).thenReturn(false);

        ItemGetResponse expectedResponse = new ItemGetResponse();
        expectedResponse.setId(item.getId());
        expectedResponse.setName("Item Name");
        expectedResponse.setDescription("Item Description");
        expectedResponse.setAvailable(true);

        when(itemConverter.itemConvertToItemGetResponse(item)).thenReturn(expectedResponse);

        ItemGetResponse response = itemService.get(itemId, userId);

        verify(bookingRepository, never()).findFirstByItemIdAndStartBeforeOrderByStartDesc(anyLong(), any());
        assertNull(response.getLastBooking());
        assertNull(response.getNextBooking());
        assertNotNull(response.getComments());
    }
}