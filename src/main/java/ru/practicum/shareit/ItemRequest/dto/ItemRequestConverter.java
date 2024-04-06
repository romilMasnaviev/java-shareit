package ru.practicum.shareit.ItemRequest.dto;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.shareit.ItemRequest.model.ItemRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemGetItemResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestConverter {

    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    ItemRequest convert(ItemRequestCreateRequest request);

    ItemRequestCreateResponse convert(ItemRequest item);

    ItemRequestGetResponse convertToGetResponse(ItemRequest item);

    List<ItemRequestGetResponse> convertToListGetResponse(List<ItemRequest> itemRequests);

    @Mapping(target = "requestId", ignore = true)
    ItemGetItemResponse itemConvertToItemGetItemRequest(Item item);

    @AfterMapping
    default void setRequestId(@MappingTarget ItemGetItemResponse response, Item item) {
        response.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
    }

}
