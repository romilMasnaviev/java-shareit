package ru.practicum.shareit.ItemRequest.service;

import ru.practicum.shareit.ItemRequest.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestCreateResponse;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestGetResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestCreateResponse create(ItemRequestCreateRequest request, Long userId);

    List<ItemRequestGetResponse> getUserItemRequests(Long userId);

    List<ItemRequestGetResponse> getUserItemRequests(Long userId, Long from, Long size);

    ItemRequestGetResponse getRequest(Long userId, Long requestId);
}