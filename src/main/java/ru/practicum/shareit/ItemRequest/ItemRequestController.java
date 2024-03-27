package ru.practicum.shareit.ItemRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestCreateResponse;
import ru.practicum.shareit.ItemRequest.dto.ItemRequestGetResponse;
import ru.practicum.shareit.ItemRequest.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String xSharerUserId = "X-Sharer-User-Id";

    private final ItemRequestService service;

    @PostMapping
    ItemRequestCreateResponse create(@RequestHeader(xSharerUserId) Long userId, ItemRequestCreateRequest request){
        return service.create(request,userId);
    }

    @GetMapping
    List<ItemRequestGetResponse> get(@RequestHeader(xSharerUserId) Long userId){
        return service.get(userId);
    }

}
