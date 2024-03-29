package ru.practicum.shareit.item.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingConverter;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@Validated
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final BookingConverter bookingConverter;
    private final ItemConverter itemConverter;
    private final CommentConverter commentConverter;

    public static void copy(Item newItem, Item oldItem) {
        if (newItem.getName() != null) oldItem.setName(newItem.getName());
        if (newItem.getDescription() != null) oldItem.setDescription(newItem.getDescription());
        if (newItem.getAvailable() != null) oldItem.setAvailable(newItem.getAvailable());
        if (newItem.getOwner() != null) oldItem.setOwner(newItem.getOwner());
        if (newItem.getRequest() != null) oldItem.setRequest(newItem.getRequest());
    }

    @Override
    public ItemResponse create(@Valid ItemCreateRequest request, Long ownerId) {
        log.info("Creating item {}", request);
        Item item = itemConverter.convert(request);
        Optional<User> optionalUser = userRepository.findById(ownerId);
        if (optionalUser.isPresent()) {
            item.setOwner(optionalUser.get());
        } else {
            throw new EntityNotFoundException("User with id " + ownerId + " not found");
        }
        return itemConverter.convert(itemRepository.save(item));
    }

    @Override
    public ItemResponse update(ItemUpdateRequest request, Long ownerId, Long itemId) {
        log.info("Updating item {} with owner id {}", request, ownerId);
        Item newItem = itemConverter.convert(request);
        Item oldItem = itemRepository.getReferenceById(itemId);
        copy(newItem, oldItem);
        checkItsItemOwner(ownerId, oldItem);
        return itemConverter.convert(itemRepository.save(oldItem));
    }

    @Override
    public ItemResponse get(Long itemId, Long userId) {
        log.info("Getting item with id {}, user id {}", itemId, userId);
        ItemResponse response = itemConverter.convert(itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new));
        if (response.getOwner().getId().equals(userId) && bookingRepository.existsBookingByItemId(itemId)) {
            response.setLastBooking(bookingConverter.convert(
                    bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(itemId, LocalDateTime.now())));
            response.setNextBooking(bookingConverter.convert(
                    bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.now())));
            checkRejectedNextBooking(response);
        }
        response.setComments(commentConverter.convert(commentRepository.findByItemId(itemId)));
        return response;
    }

    @Override
    public List<ItemResponse> getAll(Long ownerId) {
        log.info("Getting items for user with id {}", ownerId);
        List<Item> items = itemRepository.getItemsByOwnerId(ownerId);
        List<ItemResponse> itemResponses = new ArrayList<>();
        for (Item item : items) {
            ItemResponse response;
            response = itemConverter.convert(item);
            response.setLastBooking(bookingConverter.convert(
                    bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now())));
            response.setNextBooking(bookingConverter.convert(
                    bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now())));
            itemResponses.add(response);
        }
        itemResponses.sort(Comparator.comparing(ItemResponse::getId));
        return itemResponses;
    }

    @Override
    public List<ItemResponse> search(String str) {
        log.info("Searching items by keyword {}", str);
        return itemConverter.convert(searchAvailableItemsByStr(str));
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
            throw new NotFoundException("Cannot update item through a different user");
        }
    }

    private void checkRejectedNextBooking(ItemResponse response) {
        if (response.getNextBooking() != null && response.getNextBooking().getStatus().equals(Status.REJECTED)) {
            response.setNextBooking(null);
        }
    }

}
