package ru.practicum.shareit.item.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        item.setOwner(owner);
        Optional<Long> requestId = Optional.ofNullable(request.getRequestId());
        requestId.ifPresent(id -> item.setRequest(itemRequestRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Request not found"))));
        ItemResponse response = itemConverter.convert(itemRepository.save(item));
        requestId.ifPresent(response::setRequestId);
        return response;
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
        ItemResponse response = itemConverter.convert(itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new));
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
    public List<ItemResponse> getAllHub(Long ownerId, Long from, Long size) {
        if (from != null && size != null) {
            return getAllWithPagination(ownerId, from, size);
        } else {
            return getAll(ownerId);
        }
    }

    private List<ItemResponse> getAllWithPagination(Long ownerId, Long from, Long size) {
        log.info("Getting items with pagination for user with id {}", ownerId);
        validatePageParams(from, size);
        Pageable pageable = PageRequest.of((int) (from / size), size.intValue());
        List<Item> items = itemRepository.getItemsByOwnerId(ownerId, pageable);
        return getItemResponses(items);
    }

    private List<ItemResponse> getItemResponses(List<Item> items) {
        List<ItemResponse> itemResponses = new ArrayList<>();
        for (Item item : items) {
            ItemResponse response = itemConverter.convert(item);
            response.setLastBooking(bookingConverter.convert(
                    bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(),
                            LocalDateTime.now())));
            response.setNextBooking(bookingConverter.convert(
                    bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now())));
            itemResponses.add(response);
        }
        return itemResponses;
    }


    private List<ItemResponse> getAll(Long ownerId) {
        log.info("Getting all items for user with id {}", ownerId);
        List<Item> items = itemRepository.getItemsByOwnerId(ownerId);
        return getItemResponses(items);
    }

    @Override
    public List<ItemResponse> search(String str, Long from, Long size) {
        log.info("Searching items by keyword {}", str);
        Pageable pageable = createPageable(from, size);
        Page<Item> itemPage = searchAvailableItemsByStr(str, pageable);
        return itemPage.map(itemConverter::convert).getContent();
    }

    private Page<Item> searchAvailableItemsByStr(@NonNull String str, Pageable pageable) {
        if (StringUtils.isBlank(str)) {
            return Page.empty();
        }
        return itemRepository.findAllByAvailableTrueAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(str,
                str, pageable);
    }

    private Pageable createPageable(Long from, Long size) {
        if (from == null || size == null) {
            return Pageable.unpaged();
        }
        return PageRequest.of(from.intValue(), size.intValue());
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

    private void validatePageParams(Long from, Long size) {
        if (from == null || size == null || from < 0 || size <= 0) {
            throw new ru.practicum.shareit.handler.ValidationException("Invalid pagination parameters");
        }
    }

}
