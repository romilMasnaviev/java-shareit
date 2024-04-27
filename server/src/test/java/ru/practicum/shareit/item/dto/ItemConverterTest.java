package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class ItemConverterTest {

    @Autowired
    private ItemConverter itemConverter;

    @Test
    public void testItemConvertToItemCreateResponse_NullItem_ReturnsNull() {
        Item item = null;
        ItemCreateResponse response = itemConverter.itemConvertToItemCreateResponse(item);
        assertNull(response);
    }

    @Test
    public void testItemConvertToItemUpdateResponse_NullItem_ReturnsNull() {
        Item item = null;
        ItemUpdateResponse response = itemConverter.itemConvertToItemUpdateResponse(item);
        assertNull(response);
    }

    @Test
    public void testItemConvertToItemGetResponse_NullItem_ReturnsNull() {
        Item item = null;
        ItemGetResponse response = itemConverter.itemConvertToItemGetResponse(item);
        assertNull(response);
    }

    @Test
    public void testItemConvertToItemSearchResponse_NullItem_ReturnsNull() {
        Item item = null;
        ItemSearchResponse response = itemConverter.itemConvertToItemSearchResponse(item);
        assertNull(response);
    }

    @Test
    public void testItemUpdateRequestConvertToItem_NullRequest_ReturnsNull() {
        ItemUpdateRequest request = null;
        Item item = itemConverter.itemUpdateRequestConvertToItem(request);
        assertNull(item);
    }

    @Test
    public void testItemCreateRequestConvertToItem_NullRequest_ReturnsNull() {
        ItemCreateRequest request = null;
        Item item = itemConverter.itemCreateRequestConvertToItem(request);
        assertNull(item);
    }

}