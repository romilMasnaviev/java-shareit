package ru.practicum.shareit.utility;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.handler.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

class PaginationUtilTest {
    @Test
    void testGetPageable_NullFromAndSize_ReturnsUnpaged() {
        Long from = null;
        Long size = null;

        Pageable pageable = PaginationUtil.getPageable(from, size);

        assertTrue(pageable.isUnpaged());
    }

    @Test
    void testGetPageable_NonNullFromAndSize_ReturnsPageable() {
        Long from = 0L;
        Long size = 10L;

        Pageable pageable = PaginationUtil.getPageable(from, size);

        assertFalse(pageable.isUnpaged());
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }

    @Test
    void testGetPageable_NegativeFrom_ThrowsValidationException() {
        Long from = -1L;
        Long size = 10L;

        assertThrows(ValidationException.class, () -> PaginationUtil.getPageable(from, size));
    }

    @Test
    void testGetPageable_NegativeSize_ThrowsValidationException() {
        Long from = 0L;
        Long size = -10L;

        assertThrows(ValidationException.class, () -> PaginationUtil.getPageable(from, size));
    }
}