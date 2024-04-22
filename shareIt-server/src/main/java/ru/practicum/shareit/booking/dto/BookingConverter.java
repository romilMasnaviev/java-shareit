package ru.practicum.shareit.booking.dto;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingConverter {

    Booking bookingCreateRequestConvertToBooking(BookingCreateRequest request);

    @Mapping(target = "bookerId", ignore = true)
    BookingResponse bookingConvertToBookingResponse(Booking booking);

    @AfterMapping
    default void setBookerId(@MappingTarget BookingResponse response, Booking booking) {
        response.setBookerId(booking.getBooker() != null ? booking.getBooker().getId() : null);
    }

    BookingCreateResponse bookingConvertToBookingCreateResponse(Booking booking);

    BookingApproveResponse bookingConvertToBookingApproveResponse(Booking booking);

    BookingGetResponse bookingConvertToBookingGetResponse(Booking booking);

    List<BookingGetResponse> bookingConvertToBookingGetResponse(List<Booking> booking);

}
