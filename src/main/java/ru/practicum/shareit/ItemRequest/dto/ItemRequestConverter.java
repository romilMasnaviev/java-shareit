package ru.practicum.shareit.ItemRequest.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.ItemRequest.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ItemRequestConverter {

    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    ItemRequest convert(ItemRequestCreateRequest request);

    ItemRequestCreateResponse convert(ItemRequest item);

    List<ItemRequestGetResponse> convert(List<ItemRequest> itemRequests);

    ItemRequestGetResponse convertToGetResponse(ItemRequest request);
}
