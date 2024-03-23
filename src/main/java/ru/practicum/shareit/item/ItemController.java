package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String xSharerUserId = "X-Sharer-User-Id";

    private final ItemService itemService;
    private final CommentService commentService;
    private final ItemConverter converter;


    @PostMapping()
    ItemResponse create(@RequestHeader(xSharerUserId) Long userId, @RequestBody ItemCreateRequest request) {
        return converter.convert(itemService.create(request, userId));
    }

    @PatchMapping("/{itemId}")
    ItemResponse update(@RequestHeader(xSharerUserId) Long userId,
                        @RequestBody ItemUpdateRequest request,
                        @PathVariable Long itemId) {
        return converter.convert(itemService.update(request, userId, itemId));
    }

    @GetMapping("/{itemId}")
    ItemResponse get(@PathVariable Long itemId, @RequestHeader(xSharerUserId) Long userId) {
        return itemService.get(itemId, userId);
    }

    @GetMapping()
    List<ItemResponse> getAll(@RequestHeader(xSharerUserId) Long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    List<ItemResponse> search(@RequestParam String text) {
        return converter.convert(itemService.search(text));
    }

    @PostMapping("/{itemId}/comment")
    CommentResponse createComment(@RequestHeader(xSharerUserId) Long userId,
                          @PathVariable Long itemId, @RequestBody CommentCreateRequest request) { //TODO  c проверками null empty через аннотации
        return commentService.create(userId, itemId, request);
    }

}