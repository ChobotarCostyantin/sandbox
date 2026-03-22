package edu.chobotar.sandbox.service;

import edu.chobotar.sandbox.model.User;
import edu.chobotar.sandbox.repository.UserRepository;
import edu.chobotar.sandbox.request.UserCreateRequest;
import edu.chobotar.sandbox.request.UserUpdateRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/*
  @author User
  @project sandbox
  @class UserService
  @version 1.0.0
  @since 19.04.2025 - 23.06
*/

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final List<User> users = new ArrayList<>(); {
        users.add(new User("Олег Максимчук", "Tom_oleg", "maksimchuk@gmail.com", "0951234567", "Мопс"));
        users.add(new User("Валерій Адамко", "Roxaan", "adamko@gmail.com", "0957654321", "Бандеромобіль"));
        users.add(new User("Михайло Скорейко", "Tesey", "skoreyko@gmail.com", "0951237654", "Бомбардіро Крокоділо"));
        users.add(new User("В'ячеслав Москалюк", "Ikaut", "moskaliuk@gmail.com", "0985358765", "Швайн"));
    }

    @PostConstruct
    void init() {
        userRepository.deleteAll();
        for (User user: users) {
            create(user);
        }

    }

    //CRUD
    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public User create(UserCreateRequest request) {
        return mapToUser(request);
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    private User mapToUser(UserCreateRequest request) {
        return new User(request.name(), request.username(), request.email(), request.phoneNumber(), request.gender());
    }

    public User update(UserUpdateRequest request) {
        userRepository.findById(request.id()).orElseThrow(() -> new NoSuchElementException("User with id " + request.id() + " not found"));
        User userToUpdate =
                User.builder()
                        .id(request.id())
                        .name(request.name())
                        .username(request.username())
                        .email(request.email())
                        .phoneNumber(request.phoneNumber())
                        .gender(request.gender())
                        .build();
        return userRepository.save(userToUpdate);
    }

    public List<User> createAll(List<User> users) {
        return userRepository.saveAll(users);
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }

    public Boolean emailIsTaken(String email) {
        return userRepository.existsByEmail(email);
    }
}
