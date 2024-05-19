package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.utility.ControllerConstants.xSharerUserId;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping()
    public ResponseEntity<Object> create(@RequestHeader(xSharerUserId) Long userId,
                                         @Valid @RequestBody ItemCreateRequest request) {
        log.info("Creating item for user {}, request={}", userId, request);
        return itemClient.createItem(request, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(xSharerUserId) Long userId,
                                         @RequestBody ItemUpdateRequest request,
                                         @PathVariable Long itemId) {
        log.info("Updating item {} for user {}, request={}", itemId, userId, request);
        return itemClient.updateItem(request, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable Long itemId,
                                      @RequestHeader(xSharerUserId) Long userId) {
        log.info("Getting item {} for user {}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAll(@RequestHeader(xSharerUserId) Long userId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Long from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Long size) {
        log.info("Getting all items for user {} from {} with size {}", userId, from, size);
        return itemClient.getAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(xSharerUserId) Long userId,
                                         @RequestParam(name = "text") String str,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Long from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Long size) {
        log.info("Searching for items with text '{}' for user {} from {} with size {}", str, userId, from, size);
        return itemClient.search(userId, str, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(xSharerUserId) Long userId,
                                                @PositiveOrZero @PathVariable Long itemId,
                                                @Valid @RequestBody CommentCreateRequest request) {
        log.info("Creating comment for item {} by user {}, request={}", itemId, userId, request);
        return itemClient.createComment(userId, itemId, request);
    }
}
