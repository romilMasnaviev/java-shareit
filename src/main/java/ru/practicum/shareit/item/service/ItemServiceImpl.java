package ru.practicum.shareit.item.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.ItemRequest.dao.ItemRequestRepository;
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
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + ownerId + " not found"));
        item.setOwner(owner);
        Optional<Long> requestId = Optional.ofNullable(request.getRequestId());
        requestId.ifPresent(id -> item.setRequest(itemRequestRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Request with ID " + id + " not found"))));
        ItemResponse response = itemConverter.convert(itemRepository.save(item));
        requestId.ifPresent(response::setRequestId);
        return response;
    }

    @Override
    public ItemResponse update(ItemUpdateRequest request, Long ownerId, Long itemId) {
        log.info("Updating item {} with owner id {}", request, ownerId);
        Item newItem = itemConverter.convert(request);
        Item oldItem = getItem(itemId);
        copy(newItem, oldItem);
        checkItsItemOwner(ownerId, oldItem);
        return itemConverter.convert(itemRepository.save(oldItem));
    }

    @Override
    public List<ItemResponse> getAll(Long ownerId, Long from, Long size) {
        Pageable pageable = getPageable(from, size);
        log.info("Getting items with pagination for user with id {}", ownerId);
        List<Item> items = itemRepository.getItemsByOwnerId(ownerId, pageable);
        return getItemResponses(items);
    }

    @Override
    public List<ItemResponse> search(String str, Long from, Long size) {
        log.info("Searching items by keyword {}", str);
        Pageable pageable = getPageable(from, size);
        Page<Item> itemPage = searchAvailableItemsByStr(str, pageable);
        return itemPage.map(itemConverter::convert).getContent();
    }

    @Override
    public ItemResponse get(Long itemId, Long userId) {
        log.info("Getting item with id {}, user id {}", itemId, userId);
        ItemResponse response = itemConverter.convert(getItem(itemId));
        if (response.getOwner().getId().equals(userId) && bookingRepository.existsBookingByItemId(itemId)) {
            setBookingInfo(response, itemId);
        }
        response.setComments(commentConverter.convert(commentRepository.findByItemId(itemId)));
        return response;
    }

    private List<ItemResponse> getItemResponses(List<Item> items) {
        List<ItemResponse> itemResponses = new ArrayList<>();
        for (Item item : items) {
            ItemResponse response = itemConverter.convert(item);
            setBookingInfo(response, item.getId());
            itemResponses.add(response);
        }
        return itemResponses;
    }

    private void setBookingInfo(ItemResponse response, Long itemId) {
        response.setLastBooking(bookingConverter.convert(
                bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(itemId, LocalDateTime.now())));
        response.setNextBooking(bookingConverter.convert(
                bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.now())));
        checkRejectedNextBooking(response);
    }

    private void checkItsItemOwner(Long ownerId, @NonNull Item item) {
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Cannot update item through a different user");
        }
    }

    private Page<Item> searchAvailableItemsByStr(@NonNull String str, Pageable pageable) {
        if (StringUtils.isBlank(str)) {
            return Page.empty();
        }
        return itemRepository.findAllByAvailableTrueAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(str,
                str, pageable);
    }

    private void checkRejectedNextBooking(ItemResponse response) {
        if (response.getNextBooking() != null && response.getNextBooking().getStatus().equals(Status.REJECTED)) {
            response.setNextBooking(null);
        }
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item with ID " + itemId + " not found"));
    }
}
