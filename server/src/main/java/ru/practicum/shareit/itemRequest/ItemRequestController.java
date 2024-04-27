package ru.practicum.shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemRequest.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.itemRequest.dto.ItemRequestCreateResponse;
import ru.practicum.shareit.itemRequest.dto.ItemRequestGetResponse;
import ru.practicum.shareit.itemRequest.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.utility.ControllerConstants.xSharerUserId;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ItemRequestCreateResponse create(@RequestHeader(xSharerUserId) Long userId,
                                            @RequestBody(required = false) ItemRequestCreateRequest request) {
        return service.create(request, userId);
    }

    @GetMapping
    public List<ItemRequestGetResponse> get(@RequestHeader(xSharerUserId) Long userId) {
        return service.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestGetResponse> get(@RequestHeader(xSharerUserId) Long userId,
                                            @RequestParam(required = false, name = "from") Long from,
                                            @RequestParam(required = false, name = "size") Long size) {
        return service.getUserItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestGetResponse getRequest(@RequestHeader(xSharerUserId) Long userId,
                                             @PathVariable Long requestId) {
        return service.getRequest(userId, requestId);
    }

}