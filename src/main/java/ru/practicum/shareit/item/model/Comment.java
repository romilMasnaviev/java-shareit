package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "item_id")
    private Item item;
    @OneToOne
    @JoinColumn(referencedColumnName = "id", name = "user_id")
    private User author;
    @Column(name = "create_time")
    private LocalDateTime created;
}
