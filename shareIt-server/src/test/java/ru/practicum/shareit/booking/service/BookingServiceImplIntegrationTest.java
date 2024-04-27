package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.itemRequest.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.service.UserServiceImpl;

@SpringBootTest
class BookingServiceImplIntegrationTest {

    @Autowired
    BookingServiceImpl bookingService;

    @Autowired
    ItemRequestServiceImpl itemRequestService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    ItemServiceImpl itemService;

}