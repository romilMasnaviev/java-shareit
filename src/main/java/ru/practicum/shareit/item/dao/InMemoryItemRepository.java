package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.Map;

public interface InMemoryItemRepository {
    Item create(Item item);

    Item get(Long itemId);

    Item update(Item item, Long itemId);

    Map<Long, Item> getAll();
}
