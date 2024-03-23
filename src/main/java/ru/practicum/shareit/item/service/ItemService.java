package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

public interface ItemService {
    Item create(@Valid ItemCreateRequest request, Long ownerId);

    Item update(ItemUpdateRequest request, Long ownerId, Long itemId);

    ItemResponse get(Long itemId, Long userId);

    List<Item> getAll(Long ownerId);

    List<Item> search(String str);
}
