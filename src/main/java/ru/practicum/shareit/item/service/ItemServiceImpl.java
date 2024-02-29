package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.handler.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemConverter;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemConverter converter;
    private final UserRepository userRepository;

    @Override
    public Item create(ItemCreateRequest request, Long ownerId) {
        log.info("Создание вещи {}", request);
        Item item = converter.convert(request);
        checkOwnerId(ownerId);
        item.setOwner(userRepository.get(ownerId));
        checkItemFields(item);
        return itemRepository.create(item);
    }

    @Override
    public Item update(ItemUpdateRequest request, Long ownerId, Long itemId) {
        log.info("Обновление вещи {} с id владельца {}", request, ownerId);
        Item item = converter.convert(request);
        checkOwnerId(ownerId);
        checkItemExist(itemId);
        checkItsItemOwner(ownerId, itemId);
        item.setOwner(userRepository.get(ownerId));
        return itemRepository.update(item, itemId);
    }

    @Override
    public Item get(Long itemId) {
        log.info("Получение вещи с id {}", itemId);
        checkItemExist(itemId);
        return itemRepository.get(itemId);
    }

    @Override
    public List<Item> getAll(Long ownerId) {
        log.info("Получение вещей пользователя с id {}", ownerId);
        checkOwnerId(ownerId);
        return getUserItems(ownerId);
    }

    @Override
    public List<Item> search(String str) {
        log.info("Получение вещей по слову {}", str);
        return searchAvailableItemsByStr(str);
    }

    private List<Item> searchAvailableItemsByStr(String str) {
        if (str.isEmpty() || str.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.getAll().values().stream().filter(Item::getAvailable).filter(item ->
                        StringUtils.containsIgnoreCase(item.getDescription(), str) ||
                                StringUtils.containsIgnoreCase(item.getName(), str))
                .collect(Collectors.toList());
    }

    private void checkOwnerId(Long id) {
        if (id == null) {
            throw new ValidationException("Не указан id владельца вещи");
        }
        if (!userRepository.getUsers().containsKey(id)) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
    }

    private void checkItemFields(Item item) {
        checkItemHaveAvailableField(item);
        checkItemHaveNameField(item);
        checkItemHaveDescriptionField(item);
    }

    private void checkItemHaveAvailableField(Item item) {
        if (item.getAvailable() == null) {
            throw new ValidationException("Не указано поле available");
        }
    }

    private void checkItemHaveNameField(Item item) {
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ValidationException("Не указано поле name");
        }
    }

    private void checkItemHaveDescriptionField(Item item) {
        if (item.getDescription() == null) {
            throw new ValidationException("Не указано поле description");
        }
    }

    private void checkItsItemOwner(Long ownerId, Long itemId) {
        if (!userRepository.getUsers().get(ownerId).equals(itemRepository.get(itemId).getOwner())) {
            throw new NotFoundException("Нельзя обновлять вещь через другого пользователя");
        }
    }

    private void checkItemExist(Long itemId) {
        if (!itemRepository.getAll().containsKey(itemId)) {
            throw new NotFoundException("Вещь с таким id не существует");
        }
    }

    private List<Item> getUserItems(Long ownerId) {
        return itemRepository.getAll().values().stream().filter(item ->
                item.getOwner().getId().equals(ownerId)).collect(Collectors.toList());
    }
}
