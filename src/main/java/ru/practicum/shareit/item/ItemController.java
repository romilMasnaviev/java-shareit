package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemConverter;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String xSharerUserId = "X-Sharer-User-Id";

    private final ItemService service;
    private final ItemConverter converter;

    @PostMapping()
    ItemResponse create(@RequestHeader(xSharerUserId) Long userId, @RequestBody ItemCreateRequest request) {
        return converter.convert(service.create(request, userId));
    }

    @PatchMapping("/{itemId}")
    ItemResponse update(@RequestHeader(xSharerUserId) Long userId,
                        @RequestBody ItemUpdateRequest request,
                        @PathVariable Long itemId) {
        return converter.convert(service.update(request, userId, itemId));
    }

    @GetMapping("/{itemId}")
    ItemResponse get(@PathVariable Long itemId, @RequestHeader(xSharerUserId) Long userId) {
        return service.get(itemId,userId);
    }

    @GetMapping()
    List<ItemResponse> getAll(@RequestHeader(xSharerUserId) Long userId) {
        return service.getAll(userId);
    }

    @GetMapping("/search")
    List<ItemResponse> search(@RequestParam String text) {
        return converter.convert(service.search(text));
    }

}