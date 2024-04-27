package java.ru.practicum.shareit.user.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void existsByEmailAndIdNot_WhenEmailExistsBesidesGivenId_ReturnsTrue() {
        User existingUser = new User();
        existingUser.setEmail("existingEmail@mail.ru");
        existingUser.setName("existingName");
        userRepository.save(existingUser);

        boolean exists = userRepository.existsByEmailAndIdNot("existingEmail@mail.ru", existingUser.getId());

        assertFalse(exists);
    }

    @Test
    public void existsByEmailAndIdNot_WhenEmailExistsWithGivenId_ReturnsFalse() {
        User existingUser = new User();
        existingUser.setEmail("existingEmail@mail.ru");
        existingUser.setName("existingName");
        userRepository.save(existingUser);

        boolean exists = userRepository.existsByEmailAndIdNot("existingEmail@mail.ru", existingUser.getId());

        assertFalse(exists);
    }

    @Test
    public void existsByEmailAndIdNot_WhenEmailDoesNotExist_ReturnsFalse() {
        User existingUser = new User();
        existingUser.setEmail("existingEmail@mail.ru");
        existingUser.setName("existingName");
        userRepository.save(existingUser);

        boolean exists = userRepository.existsByEmailAndIdNot("nonExistingEmail@mail.ru", existingUser.getId());

        assertFalse(exists);
    }
}