package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdOrderById(Long ownerId, Pageable pageable);

    //Нет теста для метода т.к тестовая бд на H2, а запрос написан под Postgres. Более простого способа не нашел
    @Query("FROM Item WHERE available = true AND " +
            "(TRANSLATE(description, 'абвгдеёжзийклмнопрстуфхцчшщъыьэюя', 'АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ') LIKE CONCAT('%', :str, '%') OR " +
            "TRANSLATE(name, 'абвгдеёжзийклмнопрстуфхцчшщъыьэюя', 'АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ') LIKE CONCAT('%', :str, '%'))")
    List<Item> searchItemsByDescriptionOrNameIgnoreCaseContaining(@Param("str") String str, Pageable pageable);

    Boolean existsItemByOwnerIdAndId(Long ownerId, Long itemId);

}