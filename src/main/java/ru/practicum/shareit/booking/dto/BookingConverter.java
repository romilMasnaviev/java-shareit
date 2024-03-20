package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingConverter {

    Booking convert(BookingCreateRequest request);

    BookingResponse convert(Booking booking);

    List<BookingResponse> convert(List<Booking> booking);
}
