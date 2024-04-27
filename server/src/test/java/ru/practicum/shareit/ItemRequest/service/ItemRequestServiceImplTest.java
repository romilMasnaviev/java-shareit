package ru.practicum.shareit.ItemRequest.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.handler.ValidationException;
import ru.practicum.shareit.itemRequest.dao.ItemRequestRepository;
import ru.practicum.shareit.itemRequest.dto.ItemRequestConverter;
import ru.practicum.shareit.itemRequest.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.itemRequest.dto.ItemRequestCreateResponse;
import ru.practicum.shareit.itemRequest.dto.ItemRequestGetResponse;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.utility.PaginationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemRequestConverter itemRequestConverter;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    public void testCreate_SuccessfulCreation_ReturnItemRequestCreateResponse() {
        long userId = 1L;

        ItemRequestCreateRequest request = new ItemRequestCreateRequest();
        request.setDescription("Test Description");

        User user = new User();
        user.setId(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription(request.getDescription());
        itemRequest.setOwner(user);

        ItemRequestCreateResponse expectedResponse = new ItemRequestCreateResponse();
        expectedResponse.setId(itemRequest.getId());
        expectedResponse.setDescription(itemRequest.getDescription());

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(itemRequestConverter.itemRequestCreateRequestConvertToItemRequest(request)).thenReturn(itemRequest);
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
        when(itemRequestConverter.itemRequestConvertToItemRequestCreateResponse(itemRequest)).thenReturn(expectedResponse);

        ItemRequestCreateResponse actualResponse = itemRequestService.create(request, userId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testCreate_InvalidRequest_ThrowsValidationException() {
        long userId = 1L;

        ItemRequestCreateRequest request = new ItemRequestCreateRequest();

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        assertThrows(ValidationException.class, () -> itemRequestService.create(request, userId));
    }


    @Test
    public void testGetUserItemRequest_ValidData_ReturnItemRequestGetResponseList() {
        Long userId = 1L;
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(new ItemRequest());

        when(itemRequestRepository.findAllByOwner_IdOrderByCreatedDesc(userId)).thenReturn(itemRequests);

        List<ItemRequestGetResponse> expectedResponse = new ArrayList<>();
        when(itemRequestConverter.convertToListGetResponse(itemRequests)).thenReturn(expectedResponse);

        List<ItemRequestGetResponse> actualResponse = itemRequestService.getUserItemRequests(userId);

        verify(userService, times(1)).checkUserDoesntExistAndThrowIfNotFound(userId);
        verify(itemRequestRepository, times(1)).findAllByOwner_IdOrderByCreatedDesc(userId);
        verify(itemRequestConverter, times(1)).convertToListGetResponse(anyList());
        assertEquals(actualResponse, expectedResponse);
    }

    @Test
    public void testGetUserItemRequests_Pagination_ValidData_ReturnItemRequestGetResponseList() {
        Long userId = 1L;
        Long from = 0L;
        Long size = 10L;
        User user = new User();
        user.setId(1L);
        ItemRequest request = new ItemRequest();
        request.setOwner(user);
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(request);

        Pageable pageable = PaginationUtil.getPageable(from, size);

        when(itemRequestRepository.findAllByIdGreaterThanOrderByCreatedDesc(from, pageable)).thenReturn(itemRequests);

        List<ItemRequestGetResponse> expectedResponse = new ArrayList<>();
        when(itemRequestConverter.convertToListGetResponse(itemRequests)).thenReturn(expectedResponse);
        List<ItemRequestGetResponse> actualResponse = itemRequestService.getUserItemRequests(userId, from, size);

        verify(userService, times(1)).checkUserDoesntExistAndThrowIfNotFound(userId);
        verify(itemRequestRepository, times(1)).findAllByIdGreaterThanOrderByCreatedDesc(from, pageable);
        verify(itemRequestConverter, times(1)).convertToListGetResponse(anyList());
        assertEquals(actualResponse, expectedResponse);
    }


    @Test
    public void testGetRequest_ValidData_ReturnItemRequestGetResponse() {
        Long userId = 1L;
        Long requestId = 1L;

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        ItemRequestGetResponse expectedResponse = new ItemRequestGetResponse();
        when(itemRequestConverter.convertToGetResponse(itemRequest)).thenReturn(expectedResponse);

        ItemRequestGetResponse actualResponse = itemRequestService.getRequest(userId, requestId);

        verify(userService, times(1)).checkUserDoesntExistAndThrowIfNotFound(userId);
        verify(itemRequestRepository, times(1)).findById(requestId);
        verify(itemRequestConverter, times(1)).convertToGetResponse(itemRequest);
        assertEquals(actualResponse, expectedResponse);
    }
}