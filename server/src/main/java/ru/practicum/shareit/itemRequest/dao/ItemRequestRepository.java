package ru.practicum.shareit.itemRequest.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.itemRequest.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByOwner_IdOrderByCreatedDesc(Long userId);

    List<ItemRequest> findAllByIdGreaterThanOrderByCreatedDesc(Long idFrom, Pageable pageable);

}