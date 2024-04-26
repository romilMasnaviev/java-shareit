package ru.practicum.shareit.itemRequest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.handler.ValidationException;
import ru.practicum.shareit.itemRequest.dao.ItemRequestRepository;
import ru.practicum.shareit.itemRequest.dto.ItemRequestConverter;
import ru.practicum.shareit.itemRequest.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.itemRequest.dto.ItemRequestCreateResponse;
import ru.practicum.shareit.itemRequest.dto.ItemRequestGetResponse;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.service.UserService;

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
        ItemRequest item = itemRequestConverter.itemRequestCreateRequestConvertToItemRequest(request);
        item.setOwner(userRepository.getReferenceById(userId));
        return itemRequestConverter.itemRequestConvertToItemRequestCreateResponse(itemRequestRepository.save(item));
    }

    @Override
    public List<ItemRequestGetResponse> getUserItemRequests(Long userId) {
        userService.checkUserDoesntExistAndThrowIfNotFound(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByOwner_IdOrderByCreatedDesc(userId);
        return itemRequestConverter.convertToListGetResponse(itemRequests);
    }

    @Override
    public List<ItemRequestGetResponse> getUserItemRequests(Long userId, Long from, Long size) {
        userService.checkUserDoesntExistAndThrowIfNotFound(userId);
        Pageable pageable = getPageable(from, size);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByIdGreaterThanOrderByCreatedDesc(from, pageable);
        itemRequests.removeIf(request -> request.getOwner().getId().equals(userId));
        return itemRequestConverter.convertToListGetResponse(itemRequests);
    }

    @Override
    public ItemRequestGetResponse getRequest(Long userId, Long requestId) {
        userService.checkUserDoesntExistAndThrowIfNotFound(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with ID " + requestId + " not found"));
        return itemRequestConverter.convertToGetResponse(itemRequest);
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