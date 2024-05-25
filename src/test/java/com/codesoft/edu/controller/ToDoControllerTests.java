package com.codesoft.edu.controller;

import com.codesoft.edu.model.*;
import com.codesoft.edu.service.TaskService;
import com.codesoft.edu.service.ToDoService;
import com.codesoft.edu.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = ToDoController.class)
public class ToDoControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ToDoService toDoService;

    @MockBean
    TaskService taskService;

    @MockBean
    UserService userService;

    ToDo mockToDo;
    User owner;
    User collaborator;
    Task task;
    Role role;
    State state;
    List<Task> tasks;
    List<User> mockUsers;
    List<ToDo> mockToDos;
    List<User> mockCollaborators;


    @BeforeEach
    public void setUp() {

        role = new Role();
        role.setName("USER");

        owner = new User();
        owner.setId(1L);
        owner.setRole(role);
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setEmail("john.doe@gmail.com");
        owner.setPassword("Qwerty12345");

        collaborator = new User();
        collaborator.setId(2L);
        collaborator.setRole(role);
        collaborator.setFirstName("Helga");
        collaborator.setLastName("Smith");
        collaborator.setEmail("helga.smith@gmail.com");
        collaborator.setPassword("Qwerty12345");

        mockCollaborators = Arrays.asList(collaborator);

        state = new State();
        state.setName("NewState");

        task = new Task();
        task.setId(1L);
        task.setName("Task 1");
        task.setState(state);
        task.setPriority(Priority.LOW);

        mockToDo = new ToDo();
        mockToDo.setId(1L);
        mockToDo.setTitle("Test ToDo");
        mockToDo.setOwner(owner);
        mockToDo.setCreatedAt(LocalDateTime.now());
        mockToDo.setTasks(Arrays.asList(task));
        mockToDo.setCollaborators(new ArrayList<>(mockCollaborators));

        mockUsers = Arrays.asList(owner, collaborator);
        tasks = Arrays.asList(task);

        mockToDos = Arrays.asList(mockToDo);

        when(userService.readById(owner.getId())).thenReturn(owner);
        when(userService.getAll()).thenReturn(mockUsers);
        when(toDoService.getByUserId(owner.getId())).thenReturn(mockToDos);
        when(taskService.getByTodoId(mockToDo.getId())).thenReturn(tasks);

        when(toDoService.create(any(ToDo.class))).thenReturn(mockToDo);
        when(toDoService.readById(1L)).thenReturn(mockToDo);
        when(toDoService.update(any(ToDo.class))).thenReturn(mockToDo);
    }

    @Test
    @DisplayName(" Assert that mockMvc is not null - Success")
    void contextLoads() {
        assertThat(mockMvc).isNotNull();
    }


    @Test
    @DisplayName("GET /todos/create/users/{owner_id} - Success")
    void testCreateToDo() throws Exception {
        mockMvc.perform(get("/todos/create/users/{owner_id}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("create-todo"))
                .andExpect(model().attributeExists("todo"));
    }

    @Test
    @DisplayName("POST /todos/create/users/{owner_id} - Success")
    void testCreateToDoPost() throws Exception {
        mockMvc.perform(post("/todos/create/users/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "new toDo")
                        .param("createdAt", LocalDateTime.now().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/all/users/1"));
    }

    @Test
    @DisplayName("GET /{id}/tasks - Success")
    void testGetTasksByTodoId() throws Exception {
        when(toDoService.readById(mockToDo.getId())).thenReturn(mockToDo);
        when(taskService.readById(task.getId())).thenReturn(task);
        when(userService.readById(owner.getId())).thenReturn(owner);

        mockMvc.perform(get("/todos/{id}/tasks", mockToDo.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("todo-tasks"))
                .andExpect(model().attributeExists("todo", "tasks"))
                .andExpect(model().attribute("todo", mockToDo))
                .andExpect(model().attribute("tasks", tasks.stream()
                        .filter(task -> task.getId() == mockToDo.getTasks().stream()
                                .map(Task::getId)
                                .findFirst()
                                .orElse(0L))
                        .collect(Collectors.toList())));

        verify(toDoService).readById(mockToDo.getId());
        verify(taskService).getByTodoId(mockToDo.getId());
        verify(userService).getAll();
    }

    @Test
    @DisplayName("GET /todos/{todo_id}/update/users/{owner_id} - Success")
    void testUpdateToDo() throws Exception {
        mockMvc.perform(get("/todos/{todo_id}/update/users/{owner_id}", 1, 1))
                .andExpect(status().isOk())
                .andExpect(view().name("update-todo"))
                .andExpect(model().attributeExists("todo"));
    }

    @Test
    @DisplayName("POST /todos/{todo_id}/update/users/{owner_id} - Success")
    void testUpdateToDoPost() throws Exception {
        mockMvc.perform(post("/todos/1/update/users/1")
                        .param("title", "Updated Title"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/all/users/1"));
    }

    @Test
    @DisplayName("GET /todos/{todo_id}/delete/users/{owner_id} - Success")
    void testDeleteToDo() throws Exception {
        mockMvc.perform(get("/todos/{todo_id}/delete/users/{owner_id}", 1, 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/all/users/1"));
    }

    @Test
    @DisplayName("GET /todos/all/users/{user_id} - Success")
    void testGetAllToDos() throws Exception {
        when(toDoService.getByUserId(owner.getId())).thenReturn(Arrays.asList(mockToDo));

        mockMvc.perform(get("/todos/all/users/{user_id}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("todos-user"))
                .andExpect(model().attributeExists("todos", "user"))
                .andExpect(model().attribute("todos", mockToDos))
                .andExpect(model().attribute("user", owner));

        verify(toDoService).getByUserId(owner.getId());
    }

    @Test
    @DisplayName("GET /todos/{id}/add - Success")
    void testAddCollaborator() throws Exception {
        mockMvc.perform(get("/todos/{id}/add", 1)
                        .param("user_id", "2"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("GET /todos/{id}/remove - Success")
    void testRemoveCollaborator() throws Exception {
        mockMvc.perform(get("/todos/{id}/remove", 1)
                        .param("user_id", "2"))
                .andExpect(status().is3xxRedirection());
    }
}
