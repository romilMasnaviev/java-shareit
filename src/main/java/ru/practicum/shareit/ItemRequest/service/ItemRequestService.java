package ru.practicum.shareit.ItemRequest.service;

import org.mapstruct.Mapping;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestCreateResponse;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestGetResponse;

import java.util.List;


public interface ItemRequestService {
    ItemRequestCreateResponse create(ItemRequestCreateRequest request, Long userId);

    List<ItemRequestGetResponse> get(Long userId);

    List<ItemRequestGetResponse> get(Long userId, Long from, Long size);

    ItemRequestGetResponse getRequest(Long userId, Long requestId);
}