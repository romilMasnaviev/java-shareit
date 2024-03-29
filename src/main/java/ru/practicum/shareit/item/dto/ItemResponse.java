package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import ru.practicum.shareit.ItemRequest.model.ItemRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Data
public class ItemResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    @JsonIgnoreProperties("owner")
    private ItemRequest request;
    private BookingResponse lastBooking;
    private BookingResponse nextBooking;
    private List<CommentResponse> comments;
    private Long requestId;
}
