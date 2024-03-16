package ru.practicum.shareit.item.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.item.dao.JpaItemRepository;
import ru.practicum.shareit.item.dto.ItemConverter;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.JpaUserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@Validated
public class ItemServiceImpl implements ItemService {

    private final JpaItemRepository itemRepository;
    private final ItemConverter converter;
    private final JpaUserRepository userRepository;

    public static void copy(Item newItem, Item oldItem) {
        if (newItem.getName() != null) oldItem.setName(newItem.getName());
        if (newItem.getDescription() != null) oldItem.setDescription(newItem.getDescription());
        if (newItem.getAvailable() != null) oldItem.setAvailable(newItem.getAvailable());
        if (newItem.getOwner() != null) oldItem.setOwner(newItem.getOwner());
        if (newItem.getRequest() != null) oldItem.setRequest(newItem.getRequest());
    }

    @Override
    public Item create(@Valid ItemCreateRequest request, Long ownerId) {
        log.info("Создание вещи {}", request);
        Item item = converter.convert(request);
        Optional<User> optionalUser = userRepository.findById(ownerId);
        if (optionalUser.isPresent()) {
            item.setOwner(optionalUser.get());
        } else {
            throw new EntityNotFoundException("Пользователь с id " + ownerId + " не найден");
        }
        return itemRepository.save(item);
    }

    @Override
    public Item update(ItemUpdateRequest request, Long ownerId, Long itemId) {
        log.info("Обновление вещи {} с id владельца {}", request, ownerId);
        Item newItem = converter.convert(request);
        Item oldItem = itemRepository.getReferenceById(itemId);
        copy(newItem, oldItem);
        checkItsItemOwner(ownerId, oldItem);
        return itemRepository.save(oldItem);
    }

    @Override
    public Item get(Long itemId) {
        log.info("Получение вещи с id {}", itemId);
        return itemRepository.getReferenceById(itemId);
    }

    @Override
    public List<Item> getAll(Long ownerId) {
        log.info("Получение вещей пользователя с id {}", ownerId);
        return itemRepository.getItemsByOwnerId(ownerId);
    }

    @Override
    public List<Item> search(String str) {
        log.info("Получение вещей по слову {}", str);
        return searchAvailableItemsByStr(str);
    }

    private List<Item> searchAvailableItemsByStr(@NonNull String str) {
        if (str.isEmpty() || str.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findAll().stream().filter(Item::getAvailable).filter(item ->
                StringUtils.containsIgnoreCase(item.getDescription(), str) ||
                        StringUtils.containsIgnoreCase(item.getName(), str)).collect(Collectors.toList());
    }

    private void checkItsItemOwner(Long ownerId, @NonNull Item item) {
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Нельзя обновлять вещь через другого пользователя");
        }
    }

}
