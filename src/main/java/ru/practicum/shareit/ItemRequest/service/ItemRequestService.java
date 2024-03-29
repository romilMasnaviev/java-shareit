package ru.practicum.shareit.ItemRequest.service;

import ru.practicum.shareit.ItemRequest.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestCreateResponse;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestResponse;

import java.util.List;


public interface ItemRequestService {
    ItemRequestCreateResponse create(ItemRequestCreateRequest request, Long userId);

    List<ItemRequestResponse> getUserItemRequests(Long userId);

    List<ItemRequestResponse> getUserItemRequests(Long userId, Long from, Long size);

    ItemRequestResponse getRequest(Long userId, Long requestId);
}