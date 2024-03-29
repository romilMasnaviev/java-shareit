package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.handler.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentConverter;
import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Validated
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentConverter converter;

    @Override
    public CommentResponse create(Long userId, Long itemId, @Valid CommentCreateRequest request) {
        log.info("Creating comment for user ID {} and item ID {}", userId, itemId);

        checkBookingExists(userId, itemId, LocalDateTime.now());

        User author = getUser(userId);
        Item item = getItem(itemId);

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        comment.setText(request.getText());

        Comment savedComment = commentRepository.save(comment);
        return converter.convert(savedComment);
    }


    private void checkBookingExists(Long userId, Long itemId, LocalDateTime time) {
        if (!bookingRepository.existsBookingByBookerIdAndItemIdAndEndBefore(userId, itemId, time)) {
            String errorMessage = String.format("Booking not found for user with ID %d and item with ID %d", userId, itemId);
            throw new ValidationException(errorMessage);
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            String errorMessage = "User with ID " + userId + " not found";
            return new NotFoundException(errorMessage);
        });
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            String errorMessage = "Item with ID " + itemId + " not found";
            return new NotFoundException(errorMessage);
        });
    }

}
