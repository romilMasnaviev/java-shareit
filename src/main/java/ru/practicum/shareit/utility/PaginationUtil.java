package ru.practicum.shareit.utility;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PaginationUtil {

    public static Pageable getPageable(Long from, Long size) {
        if (from == null || size == null) {
            return Pageable.unpaged();
        } else if (from < 0 || size < 1) {
            throw new ru.practicum.shareit.handler.ValidationException("Invalid pagination parameters");
        } else {
            return PageRequest.of((int) (from / size), size.intValue());
        }
    }
}
