package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import javax.validation.Valid;
import java.util.List;

public interface ItemService {
    ItemCreateResponse create(@Valid ItemCreateRequest request, Long ownerId);

    ItemUpdateResponse update(ItemUpdateRequest request, Long ownerId, Long itemId);

    ItemGetResponse get(Long itemId, Long userId);

    List<ItemSearchResponse> search(String str, Long from, Long size);

    List<ItemGetResponse> getAll(Long ownerId, Long from, Long size);
}
