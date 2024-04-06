package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.utility.ControllerConstants.xSharerUserId;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping()
    ItemCreateResponse create(@RequestHeader(xSharerUserId) Long userId,
                              @RequestBody ItemCreateRequest request) {
        return itemService.create(request, userId);
    }

    @PatchMapping("/{itemId}")
    ItemUpdateResponse update(@RequestHeader(xSharerUserId) Long userId,
                              @RequestBody ItemUpdateRequest request,
                              @PathVariable Long itemId) {
        return itemService.update(request, userId, itemId);
    }

    @GetMapping("/{itemId}")
    ItemGetResponse get(@PathVariable Long itemId,
                        @RequestHeader(xSharerUserId) Long userId) {
        return itemService.get(itemId, userId);
    }

    @GetMapping()
    List<ItemGetResponse> getAll(@RequestHeader(xSharerUserId) Long userId,
                                 @RequestParam(required = false, name = "from") Long from,
                                 @RequestParam(required = false, name = "size") Long size) {
        return itemService.getAll(userId, from, size);
    }

    @GetMapping("/search")
    List<ItemSearchResponse> search(@RequestHeader(xSharerUserId) Long userId,
                                    @RequestParam(name = "text") String str,
                                    @RequestParam(required = false, name = "from") Long from,
                                    @RequestParam(required = false, name = "size") Long size) {
        return itemService.search(userId, str, from, size);
    }

    @PostMapping("/{itemId}/comment")
    CommentResponse createComment(@RequestHeader(xSharerUserId) Long userId,
                                  @PathVariable Long itemId,
                                  @RequestBody CommentCreateRequest request) {
        return commentService.create(userId, itemId, request);
    }

}