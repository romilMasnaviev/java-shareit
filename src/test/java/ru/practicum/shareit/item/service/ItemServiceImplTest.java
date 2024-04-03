package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.ItemRequest.dao.ItemRequestRepository;
import ru.practicum.shareit.ItemRequest.model.ItemRequest;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingConverter;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private BookingConverter bookingConverter;
    @Mock
    private ItemConverter itemConverter;
    @Mock
    private CommentConverter commentConverter;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemServiceImpl itemService;




    @Test
    void testCreate_ValidRequestAndOwnerExists_ReturnsItemResponse() {
        Long ownerId = 1L;
        Long requestId = 2L;
        ItemCreateRequest request = new ItemCreateRequest();
        request.setName("Test Item");
        request.setDescription("Test Description");
        request.setRequestId(requestId);

        User owner = new User();
        owner.setId(ownerId);

        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setOwner(owner);

        ItemResponse expectedResponse = new ItemResponse();
        expectedResponse.setName(item.getName());
        expectedResponse.setDescription(item.getDescription());

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemConverter.convert(request)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemConverter.convert(item)).thenReturn(expectedResponse);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(new ItemRequest()));

        ItemResponse actualResponse = itemService.create(request, ownerId);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());
        assertEquals(requestId, actualResponse.getRequestId());
        verify(userRepository, times(1)).findById(ownerId);
        verify(itemConverter, times(1)).convert(request);
        verify(itemRepository, times(1)).save(item);
        verify(itemConverter, times(1)).convert(item);
        verify(itemRequestRepository, times(1)).findById(requestId);
    }

    @Test
    public void testCreate_ValidRequestAndOwnerDoesntExist_ReturnsEntityNotFoundException() {
        Long ownerId = 1L;
        Long requestId = 2L;
        ItemCreateRequest request = new ItemCreateRequest();
        request.setName("Test Item");
        request.setDescription("Test Description");
        request.setRequestId(requestId);

        User owner = new User();
        owner.setId(ownerId);

        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setOwner(owner);

        when(userRepository.findById(ownerId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> itemService.create(request, ownerId));

    }

}