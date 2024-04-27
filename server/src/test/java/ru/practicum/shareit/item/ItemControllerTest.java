package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utility.ControllerConstants.xSharerUserId;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @MockBean
    private ItemServiceImpl itemService;
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentService commentService;

    private static List<ItemGetResponse> getItemGetResponses() {
        List<ItemGetResponse> expectedResponses = new ArrayList<>();
        ItemGetResponse item1 = new ItemGetResponse();
        item1.setId(1L);
        item1.setName("name1");
        item1.setDescription("description1");
        item1.setAvailable(true);
        expectedResponses.add(item1);

        ItemGetResponse item2 = new ItemGetResponse();
        item2.setId(2L);
        item2.setName("name2");
        item2.setDescription("description2");
        item2.setAvailable(false);
        expectedResponses.add(item2);
        return expectedResponses;
    }

    @Test
    public void testCreateItem_ValidData_ReturnItemCreateResponse() throws Exception {
        when(itemService.create(any(), any())).thenAnswer(invocation -> {
            ItemCreateResponse response = new ItemCreateResponse();
            response.setName("name");
            response.setDescription("description");
            response.setAvailable(true);
            response.setRequestId(1L);
            response.setId(1L);
            return response;
        });
        ItemCreateRequest request = new ItemCreateRequest();
        request.setName("name");
        request.setDescription("description");
        request.setAvailable(true);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(request))
                        .header(xSharerUserId, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is(request.getName())))
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.available", is(request.getAvailable())));
    }

    @Test
    public void testUpdateItem_ValidData_ReturnItemCreateResponse() throws Exception {
        when(itemService.update(any(), any(), any())).thenAnswer(invocation -> {
            ItemUpdateResponse response = new ItemUpdateResponse();
            response.setName("name");
            response.setDescription("description");
            response.setAvailable(true);
            response.setId(1L);
            return response;
        });
        ItemUpdateRequest request = new ItemUpdateRequest();
        request.setName("name");
        request.setDescription("description");
        request.setAvailable(true);
        mvc.perform(patch("/items/{itemId}", 1L)
                        .content(mapper.writeValueAsString(request))
                        .header(xSharerUserId, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is(request.getName())))
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.available", is(request.getAvailable())));
    }

    @Test
    public void testGetItem_ValidData_ReturnItemGetResponse() throws Exception {
        ItemGetResponse expectedResponse = new ItemGetResponse();
        expectedResponse.setId(1L);
        expectedResponse.setName("name");
        expectedResponse.setDescription("description");
        expectedResponse.setAvailable(true);

        when(itemService.get(anyLong(), anyLong())).thenReturn(expectedResponse);

        mvc.perform(get("/items/1")
                        .header(xSharerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    public void testGetAllItems_ValidData_ReturnItemGetResponseList() throws Exception {
        List<ItemGetResponse> expectedResponses = getItemGetResponses();

        when(itemService.getAll(any(), any(), any())).thenReturn(expectedResponses);

        mvc.perform(get("/items")
                        .header(xSharerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$[0].name", is("name1")))
                .andExpect(jsonPath("$[0].description", is("description1")))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[1].id", is(2), Integer.class))
                .andExpect(jsonPath("$[1].name", is("name2")))
                .andExpect(jsonPath("$[1].description", is("description2")))
                .andExpect(jsonPath("$[1].available", is(false)));
    }

    @Test
    public void testSearchItems_ValidData_ReturnItemSearchResponseList() throws Exception {
        List<ItemSearchResponse> expectedResponses = new ArrayList<>();
        ItemSearchResponse item1 = new ItemSearchResponse();
        item1.setId(1L);
        item1.setName("name1");
        item1.setDescription("description1");
        expectedResponses.add(item1);

        when(itemService.search(any(), any(), any(), any())).thenReturn(expectedResponses);

        mvc.perform(get("/items/search?text=name1")
                        .header(xSharerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$[0].name", is("name1")))
                .andExpect(jsonPath("$[0].description", is("description1")));
    }

    @Test
    public void testCreateComment_ValidData_ReturnCommentResponse() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setText("commentText");

        CommentResponse expectedResponse = new CommentResponse();
        expectedResponse.setId(1L);
        expectedResponse.setText("commentText");

        when(commentService.create(anyLong(), anyLong(), any(CommentCreateRequest.class))).thenReturn(expectedResponse);

        mvc.perform(post("/items/1/comment")
                        .header(xSharerUserId, 1L)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.text", is("commentText")));
    }


}