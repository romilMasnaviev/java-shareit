package ru.practicum.shareit.ItemRequest.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.itemRequest.dao.ItemRequestRepository;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@Transactional
class ItemRequestRepositoryTest {


    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Test
    public void findAllByOwner_IdOrderByCreatedDesc_WhenSingleRequestExists_ReturnsSingleRequestInDescendingOrder() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("description");
        itemRequest.setOwner(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);

        List<ItemRequest> requests = itemRequestRepository.findAllByOwner_IdOrderByCreatedDesc(user.getId());

        assertEquals(List.of(itemRequest), requests);
    }

    @Test
    public void findAllByOwner_IdOrderByCreatedDesc_WhenMultipleRequestsExist_ReturnsInDescendingOrder() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        LocalDateTime now = LocalDateTime.now();

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("description1");
        request1.setOwner(user);
        request1.setCreated(now.minusHours(2));
        itemRequestRepository.save(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("description2");
        request2.setOwner(user);
        request2.setCreated(now.minusHours(1));
        itemRequestRepository.save(request2);

        List<ItemRequest> requests = itemRequestRepository.findAllByOwner_IdOrderByCreatedDesc(user.getId());

        assertEquals(2, requests.size());
        assertEquals(request2, requests.get(0));
        assertEquals(request1, requests.get(1));
    }

    @Test
    public void findAllByOwner_IdOrderByCreatedDesc_WhenNoRequestsExist_ReturnsEmptyList() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        List<ItemRequest> requests = itemRequestRepository.findAllByOwner_IdOrderByCreatedDesc(user.getId());

        assertEquals(0, requests.size());
    }

    @Test
    public void findAllByIdGreaterThanOrderByCreatedDesc_WhenRequestsExistWithIdGreaterThan_ReturnsInDescendingOrder() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        userRepository.save(user);

        LocalDateTime now = LocalDateTime.now();

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("description1");
        request1.setOwner(user);
        request1.setCreated(now.minusHours(2));
        itemRequestRepository.save(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("description2");
        request2.setOwner(user);
        request2.setCreated(now.minusHours(1));
        itemRequestRepository.save(request2);

        List<ItemRequest> requests = itemRequestRepository.findAllByIdGreaterThanOrderByCreatedDesc(0L, Pageable.unpaged());

        assertEquals(2, requests.size());
        assertEquals(request2, requests.get(0));
        assertEquals(request1, requests.get(1));
    }

}