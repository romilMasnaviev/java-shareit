package ru.practicum.shareit.item.dao;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

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

    public Item get(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Item update(Item item, Long itemId) {
        Item itemToUpdate = items.get(itemId);
        if (item.getName() != null) itemToUpdate.setName(item.getName());
        if (item.getDescription() != null) itemToUpdate.setDescription(item.getDescription());
        if (item.getAvailable() != null) itemToUpdate.setAvailable(item.getAvailable());
        if (item.getOwner() != null) itemToUpdate.setOwner(item.getOwner());
        if (item.getRequest() != null) itemToUpdate.setRequest(item.getRequest());
        return items.get(itemId);
    }

    @Override
    public Map<Long, Item> getAll() {
        return items;
    }
}
