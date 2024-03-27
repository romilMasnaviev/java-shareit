package ru.practicum.shareit.ItemRequest.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.ItemRequest.model.ItemRequest;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
}
