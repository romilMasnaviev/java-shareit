package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
@Validated
public interface ItemConverter {
    Item convert(ItemCreateRequest request);

    ItemResponse convert(Item item);

    Item convert(ItemUpdateRequest request);

    List<ItemResponse> convert(List<Item> items);


}
