package edu.chobotar.sandbox.controller;

import edu.chobotar.sandbox.model.User;
import edu.chobotar.sandbox.request.UserCreateRequest;
import edu.chobotar.sandbox.request.UserUpdateRequest;
import edu.chobotar.sandbox.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

/*
  @author User
  @project sandbox
  @class UserRestController
  @version 1.0.0
  @since 19.04.2025 - 23.31
*/

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    @GetMapping
    public List<User> showAll() {
        return userService.getAll();
    }

    @GetMapping("{id}")
    public User showOneById(@PathVariable String id) {
        try {
            userService.getById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + " not found");
        }
        return userService.getById(id);
    }

    @PostMapping
    public User insert(@RequestBody User user) {
        return userService.create(user);
    }

    //============== request =====================
    @PostMapping("/dto")
    public User insert(@RequestBody UserCreateRequest request) {
        User user;
        try {
            user = userService.create(request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email " + request.email() + " is already in use");
        }
        return user;
    }

    @PutMapping()
    public User edit(@RequestBody User user) {
        return userService.update(user);
    }

    //============== request =====================
    @PutMapping("/dto")
    public User edit(@RequestBody UserUpdateRequest request) {
        List<User> users = userService.getAll();
            if (users.stream().anyMatch(user -> user.getEmail().equals(request.email()) && !Objects.equals(user.getId(), request.id()))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email " + request.email() + " is already in use");
            }
        return userService.update(request);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        try {
            userService.getById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + " not found");
        }
        userService.deleteById(id);
    }
}
