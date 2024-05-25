package com.codesoft.edu.controller;

import com.codesoft.edu.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class TaskControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    TaskService taskService;


    @Test
    @DisplayName("Test for getting create task form")
    public void createTaskFormTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/create/todos/7"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("task"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("todo"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("priorities"));
    }

    @Test
    @DisplayName("Test for creating task with validation errors")
    public void createTaskWithValidationErrorsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks/create/todos/7")
                        .param("name", "")
                        .param("priority", "HIGH"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("task"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("todo"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("priorities"));
    }

    @Test
    @DisplayName("Test for getting update task form")
    public void updateTaskFormTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/5/update/todos/7"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("task"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("priorities"));
    }

    @Test
    @DisplayName("Test for updating task")
    public void updateTaskTest() throws Exception {
        mockMvc.perform(post("/tasks/5/update/todos/7")
                        .param("name", "Task #5")
                        .param("priority", "HIGH")
                        .param("stateId", "8")
                        .param("todoId", "7")
                        .param("id", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/7/tasks"));
    }

    @Test
    @DisplayName("Test for updating task with validation errors")
    public void updateTaskWithValidationErrorsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks/5/update/todos/7")
                        .param("name", "")
                        .param("priority", "HIGH"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("task"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("priorities"));
    }

    @Test
    @DisplayName("Test for deleting task")
    public void deleteTaskTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/5/delete/todos/7"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/todos/7/tasks"));
    }


    @Test
    @DisplayName("Test for creating task")
    public void createTaskTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks/create/todos/7")
                        .param("name", "Task #5")
                        .param("priority", "HIGH")
                        .param("todoId", "7"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/todos/7/tasks"));
    }

}
