package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.CommentResponse;

import javax.validation.Valid;

public interface CommentService {
    CommentResponse create(Long userId, Long itemId,@Valid CommentCreateRequest request);

}
