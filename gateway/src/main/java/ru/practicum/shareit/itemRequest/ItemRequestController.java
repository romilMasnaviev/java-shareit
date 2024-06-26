package ru.practicum.shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemRequest.dto.ItemRequestCreateRequest;

import static ru.practicum.shareit.utility.ControllerConstants.xSharerUserId;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(xSharerUserId) Long userId,
                                         @RequestBody(required = false) ItemRequestCreateRequest request) {
        log.info("Creating item request for user {}, request={}", userId, request);
        return requestClient.createRequest(request, userId);
    }

    @GetMapping
    public ResponseEntity<Object> get(@RequestHeader(xSharerUserId) Long userId) {
        log.info("Getting all item requests for user {}", userId);
        return requestClient.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> get(@RequestHeader(xSharerUserId) Long userId,
                                      @RequestParam(required = false, name = "from", defaultValue = "0") Long from,
                                      @RequestParam(required = false, name = "size", defaultValue = "10") Long size) {
        log.info("Getting all item requests for user {} from {} with size {}", userId, from, size);
        return requestClient.getUserItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(xSharerUserId) Long userId,
                                             @PathVariable Long requestId) {
        log.info("Getting item request {} for user {}", requestId, userId);
        return requestClient.getRequest(userId, requestId);
    }
}
