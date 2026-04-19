package edu.chobotar.sandbox.service;

import edu.chobotar.sandbox.model.User;
import edu.chobotar.sandbox.repository.UserRepository;
import edu.chobotar.sandbox.request.UserCreateRequest;
import edu.chobotar.sandbox.request.UserPageRequest;
import edu.chobotar.sandbox.request.UserUpdateRequest;
import edu.chobotar.sandbox.response.ApiResponse;
import edu.chobotar.sandbox.response.BaseMetaData;
import edu.chobotar.sandbox.response.PaginationMetaData;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

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

//    private final List<User> users = new ArrayList<>(); {
//        users.add(new User("Олег Максимчук", "Tom_oleg", "maksimchuk@gmail.com", "0951234567", "Мопс"));
//        users.add(new User("Валерій Адамко", "Roxaan", "adamko@gmail.com", "0957654321", "Бандеромобіль"));
//        users.add(new User("Михайло Скорейко", "Tesey", "skoreyko@gmail.com", "0951237654", "Бомбардіро Крокоділо"));
//        users.add(new User("В'ячеслав Москалюк", "Ikaut", "moskaliuk@gmail.com", "0985358765", "Швайн"));
//    }

    @PostConstruct
    void init() {
//        userRepository.deleteAll();
//        for (User user: users) {
//            create(user);
//        }

    }

    //CRUD
    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(String id) {
        return userRepository.findById(id)
                .orElse(null);
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

    //---------------------------------------- 11.04.26 response impl ---------------------------------------
    public ApiResponse<BaseMetaData, User> getAllAsApiResponse() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return new ApiResponse<>(new BaseMetaData(404, false, "No users found"), null);
        }
        return new ApiResponse<>(new BaseMetaData(), users);
    }

    public ApiResponse<BaseMetaData, User> getByIdAsApiResponse(String id) {
        return userRepository.findById(id)
                .map(user -> new ApiResponse<>(new BaseMetaData(), user))
                .orElseGet(() -> new ApiResponse<>(new BaseMetaData(404, false, "User not found")));
    }

    public ApiResponse<BaseMetaData, User> createAsApiResponse(User user) {
        User newUser = userRepository.save(user);
        return new ApiResponse<>(new BaseMetaData(201, true), newUser);
    }

    public ApiResponse<BaseMetaData, User> createAllAsApiResponse(List<User> users) {
        List<User> newUsers = userRepository.saveAll(users);
        return new ApiResponse<>(new BaseMetaData(201, true), newUsers);
    }

    public ApiResponse<BaseMetaData, User> updateAsApiResponse(User user) {
        if (user.getId() == null || !userRepository.existsById(user.getId())) {
            return new ApiResponse<>(new BaseMetaData(404, false, "User not found"));
        }
        User updated = userRepository.save(user);
        return new ApiResponse<>(new BaseMetaData(), updated);
    }

    public ApiResponse<BaseMetaData, User> deleteByIdAsApiResponse(String id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.deleteById(id);
                    return new ApiResponse<>(new BaseMetaData(), user);
                })
                .orElseGet(() -> new ApiResponse<>(new BaseMetaData(404, false, "User not found")));
    }

    public ApiResponse<BaseMetaData, User> deleteAllAsApiResponse() {
        userRepository.deleteAll();
        return new ApiResponse<>(new BaseMetaData(204, true), null);
    }

    //---------------------------------------- 19.04.26 pagination impl ---------------------------------------
    public ApiResponse<PaginationMetaData, User> getUsersPage(UserPageRequest request) {
        Pageable pageable = PageRequest.of(request.page(), request.size(),
                Sort.by(Sort.Direction.DESC, "id"));

        Page<User> page = userRepository.findAll(pageable);

        PaginationMetaData metaData = new PaginationMetaData();
        metaData.setCode(200);
        metaData.setSuccess(true);
        metaData.setErrorMessage(null);

        metaData.setNumber(page.getNumber());
        metaData.setSize(page.getSize());
        metaData.setTotalElements((int) page.getTotalElements());
        metaData.setTotalPages(page.getTotalPages());
        metaData.setFirst(page.isFirst());
        metaData.setLast(page.isLast());

        if (page.getTotalElements() == 0) {
            metaData.setErrorMessage("Warning: The list is empty.");
        } else if (request.page() >= page.getTotalPages()) {
            metaData.setErrorMessage("Warning: Page value is out of range.");
            metaData.setLast(false);
        }

        return new ApiResponse<>(metaData, page.getContent());
    }
}
