package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@Validated
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
    public List<User> get() {
        log.info("Received a request to get all users");
        return userService.get();
    }

    @GetMapping("/{id}")
    public User get(
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

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10") @Positive Integer count) {
        log.info("Received a request to get recommendations to user with id {}", id);
        return userService.getRecommendations(id, count);
    }

    @GetMapping("/{fromId}/friends/common/{toId}")
    public List<User> getCommon(
            @PathVariable Long fromId,
            @PathVariable Long toId) {
        log.info("Received a request to get common friends at users with id: {} and {}", fromId, toId);
        return userService.getCommon(fromId, toId);
    }

    @DeleteMapping("/{fromId}/friends/{toId}")
    public User deleteFriend(
            @PathVariable Long fromId,
            @PathVariable Long toId) {
        log.info("Received a request to remove friendship between users with id: {} and {}", fromId, toId);
        return userService.deleteFriend(fromId, toId);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id) {
        log.info("Received a request to remove user with id: {} ", id);
        userService.delete(id);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getEvents(
            @PathVariable("id") Long id) {
        log.info("Get events {}", id);
        return userService.getEvents(id);
    }

}
