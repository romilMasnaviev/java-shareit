package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemConverter {

    ItemCreateResponse itemConvertToItemCreateResponse(Item item);

    ItemUpdateResponse itemConvertToItemUpdateResponse(Item item);

    ItemGetResponse itemConvertToItemGetResponse(Item item);

    ItemSearchResponse itemConvertToItemSearchResponse(Item item);

    Item itemUpdateRequestConvertToItem(ItemUpdateRequest request);

    Item itemCreateRequestConvertToItem(ItemCreateRequest request);

}