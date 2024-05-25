package com.codesoft.edu.controller;

import com.codesoft.edu.model.User;
import com.codesoft.edu.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.validation.BindingResult;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class UserControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Test
    @DisplayName("Test for getting all users")
    public void getAllUsersTest() throws Exception {
        List<User> expected = userService.getAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/users/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("users"))
                .andExpect(MockMvcResultMatchers.model().attribute("users", expected));
    }

    @Test
    @DisplayName("Test for creating user")
    public void createUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users/create")
                        .param("email", "test@mail.com")
                        .param("password", "Qwerty12345")
                        .param("firstName", "Mike")
                        .param("lastName", "Smith"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @Test
    @DisplayName("Test for getting user creation form")
    public void createUserFormTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/create"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"));
    }

    @Test
    @DisplayName("Test for creating user with validation errors")
    public void createUserWithValidationErrorsTest() throws Exception {
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Mockito.when(bindingResult.hasErrors()).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/create")
                        .flashAttr("org.springframework.validation.BindingResult.user", bindingResult)
                        .param("email", "testmailcom")
                        .param("password", "Q")
                        .param("firstName", "mike")
                        .param("lastName", "smith"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("create-user"));
    }

    @Test
    @DisplayName("Test for deleting user")
    public void deleteUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/4/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/users/all"));
    }


    @Test
    @DisplayName("Test for getting user by id")
    public void getUserByIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/5/read"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"));
    }

    @Test
    @DisplayName("Test for updating user")
    public void updateUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users/4/update")
                        .param("email", "johndoe@mail.com")
                        .param("password", "Qwerty12345")
                        .param("firstName", "John")
                        .param("lastName", "Smith")
                        .param("roleId", "1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/users/4/read"));
    }

    @Test
    @DisplayName("Test for getting user update form")
    public void updateUserFormTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/4/update"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"));
    }

}
