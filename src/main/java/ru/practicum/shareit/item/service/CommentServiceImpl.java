package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.JpaBookingRepository;
import ru.practicum.shareit.handler.ValidationException;
import ru.practicum.shareit.item.dao.JpaCommentRepository;
import ru.practicum.shareit.item.dao.JpaItemRepository;
import ru.practicum.shareit.item.dto.CommentConverter;
import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.dao.JpaUserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final JpaCommentRepository commentRepository;
    private final JpaBookingRepository bookingRepository;
    private final JpaUserRepository userRepository;
    private final JpaItemRepository itemRepository;
    private final CommentConverter converter;

    @Override
    public CommentResponse create(Long userId, Long itemId, CommentCreateRequest request) {
        Comment comment = new Comment();
        if (request.getText() == null || request.getText().isEmpty()) throw new ValidationException("пустое описание");
        if (bookingRepository.existsBookingByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())) {
            comment.setAuthor(userRepository.findById(userId).orElseThrow());
            comment.setItem(itemRepository.findById(itemId).orElseThrow());
            comment.setCreateTime(LocalDateTime.now());
            comment.setText(request.getText());
        } else throw new ValidationException("Бронирование не найдено.");
        CommentResponse newComment = converter.convert(commentRepository.save(comment));
        newComment.setAuthorName();
        return newComment;
    }
}
