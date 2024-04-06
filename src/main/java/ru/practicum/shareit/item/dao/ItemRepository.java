package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> getItemsByOwnerId(Long ownerId, Pageable pageable);

    List<Item> findAllByAvailableTrueAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(String description,
                                                                                                  String name,
                                                                                                  Pageable pageable);

    Boolean existsItemByOwnerIdAndId(Long ownerId, Long ItemId);

}
