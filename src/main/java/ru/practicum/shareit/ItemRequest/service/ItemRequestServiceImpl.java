package ru.practicum.shareit.ItemRequest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.ItemRequest.dao.ItemRequestRepository;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestConverter;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestCreateResponse;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestGetResponse;
import ru.practicum.shareit.ItemRequest.model.ItemRequest;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.handler.ValidationException;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    private final ItemRequestConverter converter;

    @Override
    public ItemRequestCreateResponse create(ItemRequestCreateRequest request, Long userId) {
        if(!userRepository.existsById(userId)) throw new NotFoundException("Wrong user id");
        if (request.getDescription() == null || request.getDescription().isEmpty())
            throw new ValidationException("Description must not be empty");
        ItemRequest item = converter.convert(request);
        item.setOwner(userRepository.getReferenceById(userId));
        return converter.convert(itemRequestRepository.save(item));
    }

    @Override
    public List<ItemRequestGetResponse> get(Long userId) {
        return null;
    }
}
