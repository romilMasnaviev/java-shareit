package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    public void getItemsByOwnerId_WhenUserDoesNotExist_ReturnsEmptyList() {
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(999L, Pageable.unpaged());

        assertTrue(items.isEmpty());
    }

    @Test
    public void getItemsByOwnerId_WhenUserExistsButHasNoItems_ReturnsEmptyList() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        List<Item> items = itemRepository.findAllByOwnerIdOrderById(user.getId(), Pageable.unpaged());

        assertTrue(items.isEmpty());
    }

    @Test
    public void getItemsByOwnerId_WhenUserExistsAndHasMultipleItems_ReturnsAllItems() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        Item item1 = new Item();
        item1.setOwner(user);
        item1.setName("item1");
        item1.setAvailable(true);
        item1.setDescription("description1");
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setOwner(user);
        item2.setName("item2");
        item2.setAvailable(false);
        item2.setDescription("description2");
        itemRepository.save(item2);

        List<Item> items = itemRepository.findAllByOwnerIdOrderById(user.getId(), Pageable.unpaged());
        assertEquals(2, items.size());
        assertTrue(items.contains(item1));
        assertTrue(items.contains(item2));
    }


    @Test
    public void searchItemsByDescriptionOrNameIgnoreCaseContaining_WhenMatchingItemsExist_ReturnsMatchingItems() {
        Item item1 = new Item();
        item1.setName("item1");
        item1.setDescription("Laptop");
        item1.setAvailable(true);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("item2");
        item2.setDescription("Smartphone");
        item2.setAvailable(true);
        itemRepository.save(item2);

        String searchKeyword = "Laptop";

        Pageable pageable = Pageable.unpaged();
        List<Item> itemsPage = itemRepository.findAllByAvailableTrueAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(searchKeyword, searchKeyword, pageable);

        assertEquals(1, itemsPage.size());
        assertEquals(item1, itemsPage.get(0));
    }

    @Test
    public void searchItemsByDescriptionOrNameIgnoreCaseContaining_WhenNoMatchingItemsExist_ReturnsEmptyPage() {
        Item item1 = new Item();
        item1.setName("item1");
        item1.setDescription("Laptop");
        item1.setAvailable(true);
        itemRepository.save(item1);

        String searchKeyword = "Phone";

        Pageable pageable = Pageable.unpaged();
        List<Item> itemsPage = itemRepository.findAllByAvailableTrueAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(searchKeyword, searchKeyword, pageable);

        assertEquals(0, itemsPage.size());
    }

    @Test
    public void searchItemsByDescriptionOrNameIgnoreCaseContaining_WhenKeywordInItemName_ReturnsMatchingItems() {
        Item item1 = new Item();
        item1.setName("item1");
        item1.setDescription("Laptop");
        item1.setAvailable(true);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Desktop");
        item2.setDescription("description");
        item2.setAvailable(true);
        itemRepository.save(item2);

        String searchKeyword = "Desktop";

        Pageable pageable = Pageable.unpaged();
        List<Item> itemsPage = itemRepository.findAllByAvailableTrueAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(searchKeyword, searchKeyword, pageable);

        assertEquals(1, itemsPage.size());
        assertEquals(item2, itemsPage.get(0));
    }
}

