package java.ru.practicum.shareit.ItemRequest.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ItemRequest.model.ItemRequest;
import ru.practicum.shareit.item.dto.ItemGetItemResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRequestConverterTest {

    @Autowired
    ItemRequestConverter itemConverter;

    @Test
    public void testItemConvertToItemGetItemRequest_WithNonNullFields_ReturnsItemGetItemResponse() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);

        ItemRequest request = new ItemRequest();
        request.setId(1L);

        item.setRequest(request);

        ItemGetItemResponse response = itemConverter.itemConvertToItemGetItemRequest(item);

        assertNotNull(response);

        assertEquals(item.getId(), response.getId());
        assertEquals(item.getName(), response.getName());
        assertEquals(item.getDescription(), response.getDescription());
        assertEquals(item.getAvailable(), response.getAvailable());
        assertEquals(item.getRequest().getId(), response.getRequestId());
    }

    @Test
    void itemRequestCreateRequestConvertToItemRequest_NullRequest_ReturnsNull() {
        ItemRequestCreateRequest request = null;

        ItemRequest result = itemConverter.itemRequestCreateRequestConvertToItemRequest(request);

        assertNull(result);
    }

    @Test
    void itemRequestConvertToItemRequestCreateResponse_NullItem_ReturnsNull() {
        ItemRequest item = null;

        ItemRequestCreateResponse result = itemConverter.itemRequestConvertToItemRequestCreateResponse(item);

        assertNull(result);
    }

    @Test
    void convertToGetResponse_NullItem_ReturnsNull() {
        ItemRequest item = null;

        ItemRequestGetResponse result = itemConverter.convertToGetResponse(item);

        assertNull(result);
    }

    @Test
    void convertToListGetResponse_NullList_ReturnsEmptyList() {
        List<ItemRequest> itemRequests = null;

        List<ItemRequestGetResponse> result = itemConverter.convertToListGetResponse(itemRequests);

        assertNull(result);
    }

    @Test
    void itemConvertToItemGetItemRequest_NullItem_ReturnsNull() {
        Item item = null;

        ItemGetItemResponse result = itemConverter.itemConvertToItemGetItemRequest(item);

        assertNull(result);
    }
}