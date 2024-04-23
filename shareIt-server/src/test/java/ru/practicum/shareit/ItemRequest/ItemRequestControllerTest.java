package ru.practicum.shareit.ItemRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestCreateResponse;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestGetResponse;
import ru.practicum.shareit.ItemRequest.service.ItemRequestServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utility.ControllerConstants.xSharerUserId;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    ItemRequestServiceImpl itemRequestService;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    public void testCreate_ValidData_ReturnItemRequestCreateResponse() throws Exception {
        ItemRequestCreateRequest request = new ItemRequestCreateRequest();
        request.setDescription("description");

        when(itemRequestService.create(any(), anyLong()))
                .thenAnswer(invocation -> {
                    ItemRequestCreateResponse response = new ItemRequestCreateResponse();
                    response.setDescription("description");
                    return response;
                });

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(request))
                        .header(xSharerUserId, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(request.getDescription())));
    }

    @Test
    public void testGetAllRequests_ValidData_ReturnItemRequestGetResponseList() throws Exception {
        List<ItemRequestGetResponse> expectedResponses = new ArrayList<>();
        ItemRequestGetResponse itemRequest1 = new ItemRequestGetResponse();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("description1");
        expectedResponses.add(itemRequest1);

        ItemRequestGetResponse itemRequest2 = new ItemRequestGetResponse();
        itemRequest2.setId(2L);
        itemRequest2.setDescription("description2");
        expectedResponses.add(itemRequest2);

        when(itemRequestService.getUserItemRequests(any())).thenReturn(expectedResponses);

        mvc.perform(get("/requests")
                        .header(xSharerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$[0].description", is("description1")))
                .andExpect(jsonPath("$[1].id", is(2), Integer.class))
                .andExpect(jsonPath("$[1].description", is("description2")));
    }

    @Test
    public void testGetAllRequestsPaged_ValidData_ReturnItemRequestGetResponseList() throws Exception {
        List<ItemRequestGetResponse> expectedResponses = new ArrayList<>();
        ItemRequestGetResponse itemRequest1 = new ItemRequestGetResponse();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("description1");
        expectedResponses.add(itemRequest1);

        when(itemRequestService.getUserItemRequests(anyLong(), anyLong(), anyLong())).thenReturn(expectedResponses);

        mvc.perform(get("/requests/all?from=0&size=1")
                        .header(xSharerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$[0].description", is("description1")));
    }

    @Test
    public void testGetRequestById_ValidData_ReturnItemRequestGetResponse() throws Exception {
        ItemRequestGetResponse expectedResponse = new ItemRequestGetResponse();
        expectedResponse.setId(1L);
        expectedResponse.setDescription("description");

        when(itemRequestService.getRequest(anyLong(), anyLong())).thenReturn(expectedResponse);

        mvc.perform(get("/requests/1")
                        .header(xSharerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.description", is("description")));
    }

}