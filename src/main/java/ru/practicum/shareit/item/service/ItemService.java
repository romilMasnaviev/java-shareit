package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;

import javax.validation.Valid;
import java.util.List;

public interface ItemService {
    ItemResponse create(@Valid ItemCreateRequest request, Long ownerId);

    ItemResponse update(ItemUpdateRequest request, Long ownerId, Long itemId);

    ItemResponse get(Long itemId, Long userId);

    List<ItemResponse> search(String str,Long from, Long size);

    List<ItemResponse> getAllHub(Long ownerId, Long from, Long size);
}
