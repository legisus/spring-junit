package com.codesoft.edu.service.impl;

import com.codesoft.edu.exception.NullEntityReferenceException;
import com.codesoft.edu.model.ToDo;
import com.codesoft.edu.service.ToDoService;
import com.codesoft.edu.repository.ToDoRepository;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ToDoServiceImpl implements ToDoService {

    private ToDoRepository todoRepository;

    public ToDoServiceImpl(ToDoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public ToDo create(ToDo todo) {
        try {
            return todoRepository.save(todo);
        } catch (RuntimeException e) {
            throw new NullEntityReferenceException("To-Do cannot be 'null'");
        }
    }

    @Override
    public ToDo readById(long id) {
        Optional<ToDo> optional = todoRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new EntityNotFoundException("To-Do with id " + id + " not found");
    }

    @Override
    public ToDo update(ToDo todo) {
        if (todo != null) {
            ToDo oldTodo = readById(todo.getId());
            if (oldTodo != null) {
                return todoRepository.save(todo);
            }
        }
        throw new NullEntityReferenceException("To-Do cannot be 'null'");
    }

    @Override
    public void delete(long id) {
        ToDo todo = readById(id);
        if (todo != null) {
            todoRepository.delete(todo);
        } else {
            throw new EntityNotFoundException("To-Do with id " + id + " not found");
        }
    }

    @Override
    public List<ToDo> getAll() {
        List<ToDo> todos = todoRepository.findAll();
        return todos.isEmpty() ? new ArrayList<>() : todos;
    }

    @Override
    public List<ToDo> getByUserId(long userId) {
        List<ToDo> todos = todoRepository.getByUserId(userId);
        return todos.isEmpty() ? new ArrayList<>() : todos;
    }
}
