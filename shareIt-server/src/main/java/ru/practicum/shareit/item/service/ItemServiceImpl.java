package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.itemRequest.dao.ItemRequestRepository;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utility.PaginationUtil.getPageable;

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
    private final ItemRequestRepository itemRequestRepository;

    private final BookingConverter bookingConverter;
    private final ItemConverter itemConverter;
    private final CommentConverter commentConverter;

    private final UserService userService;

    public static void copy(Item newItem, Item oldItem) {
        if (newItem.getName() != null) oldItem.setName(newItem.getName());
        if (newItem.getDescription() != null) oldItem.setDescription(newItem.getDescription());
        if (newItem.getAvailable() != null) oldItem.setAvailable(newItem.getAvailable());
        if (newItem.getOwner() != null) oldItem.setOwner(newItem.getOwner());
        if (newItem.getRequest() != null) oldItem.setRequest(newItem.getRequest());
    }

    @Override
    public ItemCreateResponse create(@Valid ItemCreateRequest request, Long ownerId) {
        log.info("Creating item {}", request);
        Item item = itemConverter.itemCreateRequestConvertToItem(request);
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new EntityNotFoundException("User with ID " + ownerId + " not found"));
        item.setOwner(owner);
        Optional<Long> requestId = Optional.ofNullable(request.getRequestId());
        requestId.ifPresent(id -> item.setRequest(itemRequestRepository.findById(id).orElseThrow(() -> new ValidationException("Request with ID " + id + " not found"))));
        ItemCreateResponse response = itemConverter.itemConvertToItemCreateResponse(itemRepository.save(item));
        requestId.ifPresent(response::setRequestId);
        return response;
    }

    @Override
    public ItemUpdateResponse update(ItemUpdateRequest request, Long ownerId, Long itemId) {
        log.info("Updating item {} with owner id {}", request, ownerId);
        userService.checkUserDoesntExistAndThrowIfNotFound(ownerId);
        Item newItem = itemConverter.itemUpdateRequestConvertToItem(request);
        Item oldItem = getItem(itemId);
        copy(newItem, oldItem);
        checkItsItemOwner(ownerId, oldItem);
        return itemConverter.itemConvertToItemUpdateResponse(itemRepository.save(oldItem));
    }

    @Override
    public List<ItemGetResponse> getAll(Long ownerId, Long from, Long size) {
        Pageable pageable = getPageable(from, size);
        log.info("Getting items with pagination for user with id {}", ownerId);
        userService.checkUserDoesntExistAndThrowIfNotFound(ownerId);
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(ownerId, pageable);
        return getItemResponses(items);
    }

    @Override
    public List<ItemSearchResponse> search(Long userId, String str, Long from, Long size) {
        log.info("Searching items by keyword {}", str);
        userService.checkUserDoesntExistAndThrowIfNotFound(userId);
        Pageable pageable = getPageable(from, size);
        List<Item> itemPage = searchAvailableItemsByStr(str, pageable);
        return itemPage.stream().map(itemConverter::itemConvertToItemSearchResponse).collect(Collectors.toList());
    }

    @Override
    public ItemGetResponse get(Long itemId, Long userId) {
        log.info("Getting item with id {}, user id {}", itemId, userId);
        userService.checkUserDoesntExistAndThrowIfNotFound(userId);
        ItemGetResponse response = itemConverter.itemConvertToItemGetResponse(getItem(itemId));
        if (itemRepository.existsItemByOwnerIdAndId(userId, itemId) && bookingRepository.existsBookingByItemId(itemId)) {
            setBookingInfo(response, itemId);
        }
        response.setComments(commentConverter.convert(commentRepository.findByItemId(itemId)));
        return response;
    }

    private List<ItemGetResponse> getItemResponses(List<Item> items) {
        List<ItemGetResponse> itemResponses = new ArrayList<>();
        for (Item item : items) {
            ItemGetResponse response = itemConverter.itemConvertToItemGetResponse(item);
            setBookingInfo(response, item.getId());
            itemResponses.add(response);
        }
        return itemResponses;
    }

    private void setBookingInfo(ItemGetResponse response, Long itemId) {
        response.setLastBooking(bookingConverter.bookingConvertToBookingResponse(bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(itemId, LocalDateTime.now())));
        response.setNextBooking(bookingConverter.bookingConvertToBookingResponse(bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.now())));
        checkRejectedNextBooking(response);
    }

    private void checkItsItemOwner(Long ownerId, Item item) {
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Cannot update item through a different user");
        }
    }

    private List<Item> searchAvailableItemsByStr(String str, Pageable pageable) {
        if (StringUtils.isBlank(str)) {
            return Collections.emptyList();
        }
        return itemRepository.searchItemsByDescriptionOrNameIgnoreCaseContaining(str.toUpperCase(), pageable);
    }

    private void checkRejectedNextBooking(ItemGetResponse response) {
        if (response.getNextBooking() != null && response.getNextBooking().getStatus().equals(Status.REJECTED)) {
            response.setNextBooking(null);
        }
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Item with ID " + itemId + " not found"));
    }
}