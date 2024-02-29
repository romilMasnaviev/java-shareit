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
    ItemResponse create(@RequestHeader(xSharerUserId) Long ownerId, @RequestBody ItemCreateRequest request) {
        return converter.convert(service.create(request, ownerId));
    }

    @PatchMapping("/{itemId}")
    ItemResponse update(@RequestHeader(xSharerUserId) Long ownerId, @RequestBody ItemUpdateRequest request, @PathVariable Long itemId) {
        return converter.convert(service.update(request, ownerId, itemId));
    }

    @GetMapping("/{itemId}")
    ItemResponse get(@PathVariable Long itemId) {
        return converter.convert(service.get(itemId));
    }

    @GetMapping()
    List<ItemResponse> getAll(@RequestHeader(xSharerUserId) Long ownerId) {
        return converter.convert(service.getAll(ownerId));
    }

    @GetMapping("/search")
    List<ItemResponse> search(@RequestParam String text) {
        return converter.convert(service.search(text));
    }

}