package ru.practicum.shareit.ItemRequest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.ItemRequest.dao.ItemRequestRepository;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestConverter;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestCreateResponse;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestGetResponse;
import ru.practicum.shareit.ItemRequest.model.ItemRequest;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.handler.ValidationException;

import ru.practicum.shareit.item.dto.ItemConverter;
import ru.practicum.shareit.item.dto.ItemGetItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
@Validated
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    private final ItemRequestConverter itemRequestConverter;
    private final ItemConverter itemConverter;

    @Override
    public ItemRequestCreateResponse create(ItemRequestCreateRequest request, Long userId) {
        log.info("Create ItemRequest {}, user id {}", request, userId);
        if (!userRepository.existsById(userId)) throw new NotFoundException("Wrong user id");
        if (request == null) throw new ValidationException("request is null");
        if (request.getDescription() == null || request.getDescription().isEmpty())
            throw new ValidationException("Description must not be empty");
        ItemRequest item = itemRequestConverter.convert(request);
        item.setCreated(LocalDateTime.now());
        item.setOwner(userRepository.getReferenceById(userId));
        return itemRequestConverter.convert(itemRequestRepository.save(item));
    }

    @Override
    public List<ItemRequestGetResponse> get(Long userId) {
        log.info("Get Users List ItemRequests , user id {}", userId);
        if (!userRepository.existsById(userId)) throw new NotFoundException("Wrong user id");
        List<ItemRequestGetResponse> getResponseList = itemRequestConverter.convert(itemRequestRepository.findAllByOwner_IdOrderByCreatedDesc(userId));
        getResponseList.forEach(response -> {
            List<ItemGetItemRequest> items = response.getItems();
            items.forEach(item -> item.setRequestId(1L));
        });
        return getResponseList;
    }

    @Override
    public List<ItemRequestGetResponse> get(Long userId, Long from, Long size) {
        log.info("Get All Users List ItemRequests , user id {}, from {}, size {}", userId, from, size);
        if (!userRepository.existsById(userId)) throw new NotFoundException("Wrong user id");
        if (from == null || size == null) return new ArrayList<>();
        if (from < 0 || size < 1) throw new ValidationException("Incorrect data ( from or size)");
        Pageable pageable = PageRequest.of(0, size.intValue());
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByIdGreaterThanOrderByCreatedDesc(from, pageable);
        itemRequests.removeIf(request -> request.getOwner().getId().equals(userId));

        return itemRequestConverter.convert(itemRequests);
    }

    @Override
    public ItemRequestGetResponse getRequest(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) throw new NotFoundException("Wrong user id");
        if(!itemRequestRepository.existsById(requestId)) throw new NotFoundException("Wrong request id");
        return itemRequestConverter.convertToGetResponse(itemRequestRepository.findById(requestId).orElseThrow(javax.validation.ValidationException::new));
    }


}
