package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private String authorName;
    private Long id;
    private String text;
    private Item item;
    private User author;
    private LocalDateTime created;

    public void setAuthorName() {
        this.authorName = this.author.getName();
    }
}
