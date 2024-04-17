package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingApproveResponse;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingCreateResponse;
import ru.practicum.shareit.booking.dto.BookingGetResponse;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utility.ControllerConstants.xSharerUserId;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    BookingServiceImpl bookingService;

    @Test
    public void testCreate_ValidData_ReturnBookingCreateResponse() throws Exception {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now());

        BookingCreateResponse response = new BookingCreateResponse();

        when(bookingService.create(any(), anyLong(), anyLong())).thenReturn(response);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(xSharerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testApproveBooking_ValidData_ReturnBookingApproveResponse() throws Exception {
        BookingApproveResponse response = new BookingApproveResponse();

        when(bookingService.approve(anyLong(), anyLong(), anyBoolean())).thenReturn(response);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(xSharerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetBooking_ValidData_ReturnBookingGetResponse() throws Exception {
        BookingGetResponse response = new BookingGetResponse();

        when(bookingService.get(anyLong(), anyLong())).thenReturn(response);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(xSharerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetUserBookings_ValidData_ReturnListOfBookingGetResponse() throws Exception {
        List<BookingGetResponse> expectedResponses = new ArrayList<>();

        when(bookingService.getUserBookings(anyLong(), anyString(), anyLong(), anyLong())).thenReturn(expectedResponses);

        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(xSharerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetOwnerBookings_ValidData_ReturnListOfBookingGetResponse() throws Exception {
        List<BookingGetResponse> expectedResponses = new ArrayList<>();

        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyLong(), anyLong())).thenReturn(expectedResponses);

        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(xSharerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}