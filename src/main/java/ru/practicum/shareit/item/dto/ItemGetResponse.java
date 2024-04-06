package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingResponse;

import java.util.List;

@Data
public class ItemGetResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingResponse lastBooking;
    private BookingResponse nextBooking;
    private List<CommentResponse> comments;
}
