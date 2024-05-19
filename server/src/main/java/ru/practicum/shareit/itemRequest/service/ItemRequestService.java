package ru.practicum.shareit.itemRequest.service;

import ru.practicum.shareit.itemRequest.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.itemRequest.dto.ItemRequestCreateResponse;
import ru.practicum.shareit.itemRequest.dto.ItemRequestGetResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestCreateResponse create(ItemRequestCreateRequest request, Long userId);

    List<ItemRequestGetResponse> getUserItemRequests(Long userId);

    List<ItemRequestGetResponse> getUserItemRequests(Long userId, Long from, Long size);

    ItemRequestGetResponse getRequest(Long userId, Long requestId);
}