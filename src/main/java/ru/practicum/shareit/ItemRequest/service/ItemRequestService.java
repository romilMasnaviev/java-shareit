package ru.practicum.shareit.ItemRequest.service;

import ru.practicum.shareit.ItemRequest.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestCreateResponse;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestGetResponse;

import java.util.List;


public interface ItemRequestService {
    ItemRequestCreateResponse create(ItemRequestCreateRequest request, Long userId);

    List<ItemRequestGetResponse> get(Long userId);
}