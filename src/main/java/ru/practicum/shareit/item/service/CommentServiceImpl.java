package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.handler.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentConverter;
import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.dao.UserRepository;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Validated
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentConverter converter;

    @Override
    public CommentResponse create(Long userId, Long itemId, @Valid CommentCreateRequest request) {
        Comment comment = new Comment();
        if (!bookingRepository.existsBookingByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())) {
            throw new ValidationException("Booking not found");
        }
        comment.setAuthor(userRepository.findById(userId).orElseThrow());
        comment.setItem(itemRepository.findById(itemId).orElseThrow());
        comment.setCreated(LocalDateTime.now());
        comment.setText(request.getText());
        CommentResponse newComment = converter.convert(commentRepository.save(comment));
        newComment.setAuthorName();
        return newComment;
    }
}
