package com.codesoft.edu.controller;

import com.codesoft.edu.dto.TaskDto;
import com.codesoft.edu.dto.TaskTransformer;
import com.codesoft.edu.model.Priority;
import com.codesoft.edu.model.Task;
import com.codesoft.edu.service.StateService;
import com.codesoft.edu.service.TaskService;
import com.codesoft.edu.service.ToDoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final ToDoService todoService;
    private final StateService stateService;

    public TaskController(TaskService taskService, ToDoService todoService, StateService stateService) {
        this.taskService = taskService;
        this.todoService = todoService;
        this.stateService = stateService;
        log.info("TaskController was created");
    }

    @GetMapping("/create/todos/{todo_id}")
    public String create(@PathVariable("todo_id") long todoId, Model model) {
        model.addAttribute("task", new TaskDto());
        model.addAttribute("todo", todoService.readById(todoId));
        model.addAttribute("priorities", Priority.values());
        log.info("@GetMapping: TaskController.create() was called");
        log.info("return: create-task");
        return "create-task";
    }

    @PostMapping("/create/todos/{todo_id}")
    public String create(@PathVariable("todo_id") long todoId, Model model,
                         @Validated @ModelAttribute("task") TaskDto taskDto, BindingResult result) {
        log.info("@PostMapping TaskController.create() was called");
        if (result.hasErrors()) {
            log.info("TaskController.create(): result.hasErrors()");
            model.addAttribute("todo", todoService.readById(todoId));
            model.addAttribute("priorities", Priority.values());
            log.info("Try to create task again");
            log.info("return: create-task");
            return "create-task";
        }
        Task task = TaskTransformer.convertToEntity(
                taskDto,
                todoService.readById(taskDto.getTodoId()),
                stateService.getByName("New")
        );
        log.info("Task for creation has been created: " + task);
        taskService.create(task);
        log.info("Task has been created in service level");
        log.info("return: redirect:/todos/todoId/tasks");
        return "redirect:/todos/" + todoId + "/tasks";
    }

    @GetMapping("/{task_id}/update/todos/{todo_id}")
    public String update(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId, Model model) {
        log.info("@GetMapping: TaskController.update() was called");
        TaskDto taskDto = TaskTransformer.convertToDto(taskService.readById(taskId));
        log.info("Task DTO for updating has been created: " + taskDto);
        model.addAttribute("task", taskDto);
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("states", stateService.getAll());
        log.info("Task for updating has been added to model");
        log.info("return: update-task");
        return "update-task";
    }

    @PostMapping("/{task_id}/update/todos/{todo_id}")
    public String update(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId, Model model,
                         @Validated @ModelAttribute("task")TaskDto taskDto, BindingResult result) {
        log.info("@PostMapping: TaskController.update() was called");
        if (result.hasErrors()) {
            log.info("TaskController.update(): result.hasErrors()");
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("states", stateService.getAll());
            log.info("Try to update task again");
            log.info("return: update-task");
            return "update-task";
        }
        Task task = TaskTransformer.convertToEntity(
                taskDto,
                todoService.readById(taskDto.getTodoId()),
                stateService.readById(taskDto.getStateId())
        );
        log.info("Task for updating has been created: " + task);
        taskService.update(task);
        log.info("Task has been updated in service level");
        log.info("return: redirect:/todos/todoId/tasks");
        return "redirect:/todos/" + todoId + "/tasks";
    }

    @GetMapping("/{task_id}/delete/todos/{todo_id}")
    public String delete(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId) {
        log.info("@GetMapping: TaskController.delete() was called");
        taskService.delete(taskId);
        log.info("Task has been deleted in service level");
        log.info("return: redirect:/todos/todoId/tasks");
        return "redirect:/todos/" + todoId + "/tasks";
    }
}
