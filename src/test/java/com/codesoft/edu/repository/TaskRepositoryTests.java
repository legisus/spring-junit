package com.codesoft.edu.repository;

import com.codesoft.edu.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class TaskRepositoryTests {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Task task;

    @BeforeEach
    public void init() {
        task = new Task();
        task.setName("Test Task");

        entityManager.persist(task);
    }

    @Test
    @DisplayName("Save task test")
    public void saveTaskTest() {
        Task savedTask = taskRepository.save(task);
        Assertions.assertNotNull(savedTask.getId(), "Task should be saved and have an ID");
    }

    @Test
    @DisplayName("Get task test")
    public void getTaskTest() {
        Task savedTask = taskRepository.save(task);
        Task retrievedTask = taskRepository.findById(savedTask.getId()).orElse(null);
        Assertions.assertNotNull(retrievedTask, "Task should be retrieved from the database");
    }

    @Test
    @DisplayName("Delete task test")
    public void deleteTaskTest() {
        Task savedTask = taskRepository.save(task);
        taskRepository.deleteById(savedTask.getId());
        Task retrievedTask = taskRepository.findById(savedTask.getId()).orElse(null);
        Assertions.assertNull(retrievedTask, "Task should be deleted from the database");
    }

    @Test
    @DisplayName("Update task test")
    public void updateTaskTest() {
        Task savedTask = taskRepository.save(task);
        savedTask.setName("Updated Task");
        Task updatedTask = taskRepository.save(savedTask);
        Assertions.assertEquals("Updated Task", updatedTask.getName(), "Task should be updated in the database");
    }

    @Test
    @DisplayName("Get all tasks test")
    public void getAllTasksTest() {
        Task task2 = new Task();
        task2.setName("Test Task 2");
        entityManager.persist(task2);

        Iterable<Task> tasks = taskRepository.findAll();
        Assertions.assertTrue(tasks.iterator().hasNext(), "There should be tasks in the database");
    }

}