package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public User create(
            @Valid
            @RequestBody User user) {
        log.info("Received a request to add a new user");
        return userService.create(user);
    }

    @PutMapping
    public User put(
            @Valid
            @RequestBody User user) {
        log.info("Received a request to update a user with id {}", user.getId());
        return userService.update(user);
    }

    @PutMapping("/{fromId}/friends/{toId}")
    public User addFriend(
            @PathVariable Long fromId,
            @PathVariable Long toId) {
        log.info("Received a request to add {} to friends {}", fromId, toId);
        return userService.addFriend(fromId, toId);
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Received a request to get all users");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(
            @PathVariable Long id) {
        log.info("Received a request to get user with id: {} ", id);
        return userService.get(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(
            @PathVariable Long id) {
        log.info("Received a request to get friends user with id {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{fromId}/friends/common/{toId}")
    public List<User> mutualFriends(
            @PathVariable Long fromId,
            @PathVariable Long toId) {
        log.info("Received a request to get common friends at users with id: {} and {}", fromId, toId);
        return userService.mutualFriends(fromId, toId);
    }

    @DeleteMapping("/{fromId}/friends/{toId}")
    public User deleteFriend(
            @PathVariable Long fromId,
            @PathVariable Long toId) {
        log.info("Received a request to remove friendship between users with id: {} and {}", fromId, toId);
        return userService.deleteFriend(fromId, toId);
    }

}