package ru.practicum.shareit.ItemRequest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.ItemRequest.dao.ItemRequestRepository;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestConverter;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestCreateResponse;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestResponse;
import ru.practicum.shareit.ItemRequest.model.ItemRequest;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.handler.ValidationException;
import ru.practicum.shareit.item.dto.ItemGetItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.utility.PaginationUtil.getPageable;

@RequiredArgsConstructor
@Service
@Transactional
@Validated
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    private final ItemRequestConverter itemRequestConverter;

    private final UserService userService;

    @Override
    public ItemRequestCreateResponse create(ItemRequestCreateRequest request, Long userId) {
        log.info("Creating ItemRequest {}, user id {}", request, userId);
        userService.checkUserDoesntExistAndThrowIfNotFound(userId);
        checkItemRequestIsCorrect(request);
        ItemRequest item = itemRequestConverter.convert(request);
        item.setOwner(userRepository.getReferenceById(userId));
        return itemRequestConverter.convert(itemRequestRepository.save(item));
    }

    @Override
    public List<ItemRequestResponse> getUserItemRequests(Long userId) {
        log.info("Getting Users List of ItemRequests, user id {}", userId);
        userService.checkUserDoesntExistAndThrowIfNotFound(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByOwner_IdOrderByCreatedDesc(userId);
        return getRequestsForUser(itemRequests);
    }

    @Override
    public List<ItemRequestResponse> getUserItemRequests(Long userId, Long from, Long size) {
        log.info("Getting All Users List of ItemRequests, user id {}, from {}, size {}", userId, from, size);
        userService.checkUserDoesntExistAndThrowIfNotFound(userId);
        Pageable pageable = getPageable(from, size);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByIdGreaterThanOrderByCreatedDesc(from, pageable);
        itemRequests.removeIf(request -> request.getOwner().getId().equals(userId));
        return getRequestsForUser(itemRequests);
    }

    @Override
    public ItemRequestResponse getRequest(Long userId, Long requestId) {
        log.info("Getting request with ID {} for user with ID {}", requestId, userId);
        userService.checkUserDoesntExistAndThrowIfNotFound(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with ID " + requestId + " not found"));
        return mapToItemRequestResponse(itemRequest);
    }

    private List<ItemRequestResponse> getRequestsForUser(List<ItemRequest> itemRequests) {
        List<ItemRequestResponse> responseList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            ItemRequestResponse response = mapToItemRequestResponse(itemRequest);
            responseList.add(response);
        }
        return responseList;
    }

    private ItemRequestResponse mapToItemRequestResponse(ItemRequest itemRequest) {
        ItemRequestResponse response = new ItemRequestResponse();
        response.setId(itemRequest.getId());
        response.setDescription(itemRequest.getDescription());
        response.setCreated(itemRequest.getCreated());
        List<ItemGetItemRequest> itemResponses = new ArrayList<>();
        for (Item item : itemRequest.getItems()) {
            ItemGetItemRequest itemResponse = mapToItemRequestResponse(item);
            itemResponses.add(itemResponse);
        }
        response.setItems(itemResponses);
        return response;
    }

    private ItemGetItemRequest mapToItemRequestResponse(Item item) {
        ItemGetItemRequest response = new ItemGetItemRequest();
        response.setId(item.getId());
        response.setName(item.getName());
        response.setDescription(item.getDescription());
        response.setAvailable(item.getAvailable());
        response.setRequestId(item.getRequest().getId());
        return response;
    }

    private void checkItemRequestIsCorrect(ItemRequestCreateRequest request) {
        if (request == null) {
            throw new ValidationException("Request is null");
        }
        if (request.getDescription() == null || request.getDescription().isEmpty()) {
            throw new ValidationException("Description must not be empty");
        }
    }

}
