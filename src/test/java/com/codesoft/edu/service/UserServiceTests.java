package com.codesoft.edu.service;


import com.codesoft.edu.exception.NullEntityReferenceException;
import com.codesoft.edu.repository.UserRepository;
import com.codesoft.edu.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class UserServiceTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Test
    @DisplayName("Test for creating null user")
    public void createUserWithNullUserTest() {
        UserRepository mockRepository = Mockito.mock(UserRepository.class);
        Mockito.when(mockRepository.save(null)).thenThrow(IllegalArgumentException.class);

        userService = new UserServiceImpl(mockRepository);

        Executable executable = () -> userService.create(null);

        assertThrows(NullEntityReferenceException.class, executable);
    }

    @Test
    @DisplayName("Test for reading user by not existing id")
    public void readUserByNotExistingIdTest() {
        UserRepository mockRepository = Mockito.mock(UserRepository.class);
        Mockito.when(mockRepository.findById(1L)).thenReturn(null);

        userService = new UserServiceImpl(mockRepository);

        Executable executable = () -> userService.readById(1L);

        assertThrows(NullPointerException.class, executable);
    }

    @Test
    @DisplayName("Test for updating null user")
    public void updateUserWithNullUserTest() {
        UserRepository mockRepository = Mockito.mock(UserRepository.class);
        Mockito.when(mockRepository.save(null)).thenThrow(IllegalArgumentException.class);

        userService = new UserServiceImpl(mockRepository);

        Executable executable = () -> userService.update(null);

        assertThrows(NullEntityReferenceException.class, executable);
    }

    @Test
    @DisplayName("Test for reading user with not existing id")
    public void readUserWithNotExistingIdTest() {
        UserRepository mockRepository = Mockito.mock(UserRepository.class);
        Mockito.when(mockRepository.findById(1L)).thenReturn(Optional.empty());

        userService = new UserServiceImpl(mockRepository);

        Executable executable = () -> userService.readById(1L);

        assertThrows(EntityNotFoundException.class, executable);
    }


    @Test
    @DisplayName("Test for deleting user by not existing id")
    public void deleteUserByNotExistingIdTest() {
        UserRepository mockRepository = Mockito.mock(UserRepository.class);
        Mockito.when(mockRepository.findById(1L)).thenReturn(Optional.empty());

        userService = new UserServiceImpl(mockRepository);

        Executable executable = () -> userService.delete(1L);

        assertThrows(EntityNotFoundException.class, executable);
    }


    @Test
    @DisplayName("Test for getting all users when there are no users")
    public void getAllUsersWhenNoUsersTest() {
        UserRepository mockRepository = Mockito.mock(UserRepository.class);
        Mockito.when(mockRepository.findAll()).thenReturn(new ArrayList<>());

        userService = new UserServiceImpl(mockRepository);

        assert userService.getAll().isEmpty();
    }

}

