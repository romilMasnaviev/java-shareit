package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

public interface CommentService {
    CommentResponse create(Long userId, Long itemId, @Valid CommentCreateRequest request);

}
