package ru.practicum.shareit.ItemRequest.model;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {

    @Id
    private Long id;
    private String description;
    private LocalDateTime created;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id", name = "requester_id")
    private User owner;
    @OneToMany(mappedBy = "request")
    private List<Item> items;
}
