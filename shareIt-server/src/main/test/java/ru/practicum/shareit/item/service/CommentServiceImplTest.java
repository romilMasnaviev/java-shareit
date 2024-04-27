package java.ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.handler.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentConverter;
import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentServiceImpl;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentConverter converter;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    public void testCreate_SuccessfulCommentCreation_ReturnsCommentResponse() {
        Long userId = 1L;
        Long itemId = 2L;
        String commentText = "Great product!";
        CommentCreateRequest request = new CommentCreateRequest();
        request.setText(commentText);

        User user = new User();
        user.setId(userId);

        Item item = new Item();
        item.setId(itemId);

        doNothing().when(userService).checkUserDoesntExistAndThrowIfNotFound(any());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.existsBookingByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(true);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));


        Comment savedComment = new Comment();
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentResponse expectedResponse = new CommentResponse();
        when(converter.convert(savedComment)).thenReturn(expectedResponse);

        CommentResponse actualResponse = commentService.create(userId, itemId, request);

        assertEquals(expectedResponse, actualResponse);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void testCreate_BookingNotFound_ThrowsNotFoundException() {
        Long userId = 1L;
        Long itemId = 2L;
        String commentText = "text";
        CommentCreateRequest request = new CommentCreateRequest();
        request.setText(commentText);

        User user = new User();
        user.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        when(bookingRepository.existsBookingByBookerIdAndItemIdAndEndBefore(any(), any(), any())).thenReturn(false);

        assertThrows(ValidationException.class, () -> commentService.create(userId, itemId, request));
        verify(commentRepository, never()).save(any(Comment.class));
    }
}