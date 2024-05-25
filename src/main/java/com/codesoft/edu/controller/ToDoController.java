package com.codesoft.edu.controller;

import com.codesoft.edu.model.Task;
import com.codesoft.edu.model.ToDo;
import com.codesoft.edu.model.User;
import com.codesoft.edu.service.TaskService;
import com.codesoft.edu.service.ToDoService;
import com.codesoft.edu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/todos")
public class ToDoController {

    private final ToDoService todoService;
    private final TaskService taskService;
    private final UserService userService;

    public ToDoController(ToDoService todoService, TaskService taskService, UserService userService) {
        this.todoService = todoService;
        this.taskService = taskService;
        this.userService = userService;
        log.info("ToDoController was created");
    }

    @GetMapping("/create/users/{owner_id}")
    public String create(@PathVariable("owner_id") long ownerId, Model model) {
        log.info("@GetMapping: Create ToDo for user with id: " + ownerId);
        model.addAttribute("todo", new ToDo());
        model.addAttribute("ownerId", ownerId);
        log.info("return: create-todo");
        return "create-todo";
    }

    @PostMapping("/create/users/{owner_id}")
    public String create(@PathVariable("owner_id") long ownerId, @Validated @ModelAttribute("todo") ToDo todo, BindingResult result) {
        log.info("@PostMapping: Create ToDo for user with id: " + ownerId);
        if (result.hasErrors()) {
            log.error("Validation error: " + result.getAllErrors());
            log.info("Try to create todo again");
            return "create-todo";
        }
        todo.setCreatedAt(LocalDateTime.now());
        todo.setOwner(userService.readById(ownerId));
        log.info("Try to create todo: " + todo);
        todoService.create(todo);
        log.info("Todo has been created: " + todo);
        log.info("return: redirect:/todos/all/users/ownerId");
        return "redirect:/todos/all/users/" + ownerId;
    }

    @GetMapping("/{id}/tasks")
    public String read(@PathVariable long id, Model model) {
        log.info("@GetMapping: Read ToDo with id: " + id);
        ToDo todo = todoService.readById(id);
        log.info("From service lever received todo: " + todo);
        List<Task> tasks = taskService.getByTodoId(id);
        log.info("From service lever received tasks: " + tasks);
        List<User> users = userService.getAll().stream()
                .filter(user -> user.getId() != todo.getOwner().getId()).collect(Collectors.toList());
        log.info("From service lever received users: " + users);
        model.addAttribute("todo", todo);
        model.addAttribute("tasks", tasks);
        model.addAttribute("users", users);
        log.info("return: todo-tasks");
        return "todo-tasks";
    }

    @GetMapping("/{todo_id}/update/users/{owner_id}")
    public String update(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId, Model model) {
        log.info("@GetMapping: Update ToDo with id: " + todoId);
        ToDo todo = todoService.readById(todoId);
        log.info("From service lever received todo: " + todo);
        model.addAttribute("todo", todo);
        log.info("return: update-todo");
        return "update-todo";
    }

    @PostMapping("/{todo_id}/update/users/{owner_id}")
    public String update(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId,
                         @Validated @ModelAttribute("todo") ToDo todo, BindingResult result) {
        log.info("@PostMapping: Update ToDo with id: " + todoId);
        if (result.hasErrors()) {
            log.error("Validation error: " + result.getAllErrors());
            todo.setOwner(userService.readById(ownerId));
            log.info("Try to update todo again");
            return "update-todo";
        }
        ToDo oldTodo = todoService.readById(todoId);
        log.info("From service lever received old todo: " + oldTodo);
        todo.setOwner(oldTodo.getOwner());
        todo.setCollaborators(oldTodo.getCollaborators());
        log.info("Try to update todo: " + todo);
        todoService.update(todo);
        log.info("Todo has been updated successfully");
        log.info("redirect:/todos/all/users/ownerId");
        return "redirect:/todos/all/users/" + ownerId;
    }

    @GetMapping("/{todo_id}/delete/users/{owner_id}")
    public String delete(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId) {
        log.info("@GetMapping: Delete ToDo with id: " + todoId);
        todoService.delete(todoId);
        log.info("Todo has been deleted successfully");
        log.info("redirect:/todos/all/users/ownerId");
        return "redirect:/todos/all/users/" + ownerId;
    }

    @GetMapping("/all/users/{user_id}")
    public String getAll(@PathVariable("user_id") long userId, Model model) {
        log.info("@GetMapping: Get all ToDos for user with id: " + userId);
        List<ToDo> todos = todoService.getByUserId(userId);
        log.info("From service lever received todos: " + todos);
        model.addAttribute("todos", todos);
        model.addAttribute("user", userService.readById(userId));
        log.info("return: todos-user");
        return "todos-user";
    }

    @GetMapping("/{id}/add")
    public String addCollaborator(@PathVariable long id, @RequestParam("user_id") long userId) {
        log.info("@GetMapping: Add collaborator to ToDo with id: " + id);
        ToDo todo = todoService.readById(id);
        log.info("From service lever received todo: " + todo);
        List<User> collaborators = todo.getCollaborators();
        log.info("From service lever received collaborators: " + collaborators);
        collaborators.add(userService.readById(userId));
        log.info("Try to add collaborator to todo: " + todo);
        todo.setCollaborators(collaborators);
        log.info("Try to update todo: " + todo);
        todoService.update(todo);
        log.info("ToDo has been updated successfully");
        log.info("redirect:/todos/id/tasks");
        return "redirect:/todos/" + id + "/tasks";
    }

    @GetMapping("/{id}/remove")
    public String removeCollaborator(@PathVariable long id, @RequestParam("user_id") long userId) {
        log.info("@GetMapping: Remove collaborator from ToDo with id: " + id);
        ToDo todo = todoService.readById(id);
        log.info("From service lever received todo: " + todo);
        List<User> collaborators = todo.getCollaborators();
        collaborators.remove(userService.readById(userId));
        todo.setCollaborators(collaborators);
        log.info("Try to remove collaborator from todo: " + todo);
        todoService.update(todo);
        log.info("ToDo has been updated successfully");
        log.info("redirect:/todos/id/tasks");
        return "redirect:/todos/" + id + "/tasks";
    }
}
