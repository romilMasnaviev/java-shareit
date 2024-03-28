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
    ItemRequestCreateResponse create(@RequestHeader(xSharerUserId) Long userId,@RequestBody(required = false) ItemRequestCreateRequest request) {
        return service.create(request, userId);
    }

    @GetMapping
    List<ItemRequestGetResponse> get(@RequestHeader(xSharerUserId) Long userId) {
        return service.get(userId);
    }

    @GetMapping("/all")
    List<ItemRequestGetResponse> get(@RequestHeader(xSharerUserId) Long userId,
                                     @RequestParam(required = false, name = "from") Long from,
                                     @RequestParam(required = false, name = "size") Long size) {
        return service.get(userId, from, size);
    }

    @GetMapping("/{requestId}")
    ItemRequestGetResponse getRequest(@RequestHeader(xSharerUserId) Long userId, @PathVariable Long requestId){
        return service.getRequest(userId,requestId);
    }

}
