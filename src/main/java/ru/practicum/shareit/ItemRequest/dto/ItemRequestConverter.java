package ru.practicum.shareit.ItemRequest.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.ItemRequest.model.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemRequestConverter {
    ItemRequest convert(ItemRequestCreateRequest request);

    ItemRequestCreateResponse convert(ItemRequest item);
}
