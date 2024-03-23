package ru.practicum.shareit.item.dao;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.HashMap;
import java.util.Map;

@Getter
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;

    @Override
    public Item create(Item item) {
        item.setId(id);
        items.put(id, item);
        return items.get(id++);
    }

    @Override
    public Item get(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Item update(Item item, Long itemId) {
        Item itemToUpdate = items.get(itemId);
        ItemServiceImpl.copy(item, itemToUpdate);
        return items.get(itemId);
    }

    @Override
    public Map<Long, Item> getAll() {
        return items;
    }
}
