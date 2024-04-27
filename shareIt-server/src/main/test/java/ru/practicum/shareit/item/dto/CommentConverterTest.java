package java.ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentConverter;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CommentConverterTest {

    @Autowired
    CommentConverter converter;

    @Test
    public void testConvert_WithNonNullFields_ReturnsCommentResponse() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setCreated(LocalDateTime.now());

        Item item = new Item();
        item.setId(1L);
        User author = new User();
        author.setId(1L);
        author.setName("John Doe");

        comment.setItem(item);
        comment.setAuthor(author);

        CommentResponse response = converter.convert(comment);

        assertNotNull(response);

        assertEquals(comment.getId(), response.getId());
        assertEquals(comment.getText(), response.getText());
        assertEquals(comment.getCreated(), response.getCreated());
        assertEquals(comment.getItem(), response.getItem());
        assertEquals(comment.getAuthor(), response.getAuthor());
        assertEquals(comment.getAuthor().getName(), response.getAuthorName());
    }

    @Test
    public void testConvertList_WithNonNullFields_ReturnsListCommentResponse() {
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setText("Test comment 1");
        comment1.setCreated(LocalDateTime.now());

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setText("Test comment 2");
        comment2.setCreated(LocalDateTime.now());

        List<Comment> comments = Arrays.asList(comment1, comment2);

        Item item = new Item();
        item.setId(1L);
        User author = new User();
        author.setId(1L);
        author.setName("John Doe");

        comment1.setItem(item);
        comment1.setAuthor(author);
        comment2.setItem(item);
        comment2.setAuthor(author);

        List<CommentResponse> responses = converter.convert(comments);

        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals(comment1.getId(), responses.get(0).getId());
        assertEquals(comment1.getText(), responses.get(0).getText());
        assertEquals(comment1.getCreated(), responses.get(0).getCreated());
        assertEquals(comment1.getItem(), responses.get(0).getItem());
        assertEquals(comment1.getAuthor(), responses.get(0).getAuthor());
        assertEquals(comment1.getAuthor().getName(), responses.get(0).getAuthorName());

        assertEquals(comment2.getId(), responses.get(1).getId());
        assertEquals(comment2.getText(), responses.get(1).getText());
        assertEquals(comment2.getCreated(), responses.get(1).getCreated());
        assertEquals(comment2.getItem(), responses.get(1).getItem());
        assertEquals(comment2.getAuthor(), responses.get(1).getAuthor());
        assertEquals(comment2.getAuthor().getName(), responses.get(1).getAuthorName());
    }
}