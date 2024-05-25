package com.codesoft.edu.repository;

import com.codesoft.edu.model.Role;
import com.codesoft.edu.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;
    private Role userRole;
    private User user;

    @BeforeEach
    public void init() {
        userRole = new Role();
        userRole.setName("USER");
        entityManager.persist(userRole);

        user = new User();
        user.setRole(userRole);
        user.setEmail("johndoe@mail.com");
        user.setPassword("Qwerty12345");
        user.setFirstName("John");
        user.setLastName("Doe");

    }

    @Test
    public void newUserTest() {
        entityManager.persist(user);

        User actual = userRepository.getUserByEmail("johndoe@mail.com");
        Assertions.assertEquals("John", actual.getFirstName(), "User should be added to database");
    }

    @Test
    public void updateUserTest() {
        User saved = entityManager.persist(user);
        saved.setLastName("Smith");

        User updated = userRepository.save(saved);
        Assertions.assertEquals("Smith", updated.getLastName(), "User should be updated in database");
    }

    @Test
    public void deleteUserTest() {
        User saved = entityManager.persist(user);
        userRepository.delete(saved);

        User actual = entityManager.find(User.class, saved.getId());

        Assertions.assertNull(actual, "User should be deleted from database");
    }

    @Test
    public void getUserByEmailTest() {
        User saved = entityManager.persist(user);
        User actual = userRepository.getUserByEmail("johndoe@mail.com");
        Assertions.assertEquals(saved, actual, "User should be found by email");
    }

    @Test
    public void getUserByEmailTestShouldReturnNull() {
        User actual = userRepository.getUserByEmail("notexisting@mail.com");
        Assertions.assertNull(actual, "User should not be found by email");
    }
}
