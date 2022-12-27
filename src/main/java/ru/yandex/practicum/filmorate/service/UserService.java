package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.dictionary.EventOperation;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collections;
import java.util.List;

/**
 * Responsible for such operations with users,
 * like adding to friends, removing from friends, displaying a list of mutual friends.
 * While users do not need to approve friend requests, we add them right away.
 * That is, if Lena became Sasha's friend, then this means that Sasha is now Lena's friend.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final EventStorage eventStorage;
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;

    public User addFriend(Long fromUser, Long toUser) {
        log.info("Adding a user with an id = {}, as a friend to a user with an id = {}", toUser, fromUser);
        friendStorage.add(fromUser, toUser);
        Event event = new Event(fromUser, EventType.FRIEND, EventOperation.ADD, toUser);
        eventStorage.create(event);
        log.info("Added the 'Add to Friends' event.");
        return get(fromUser);
    }

    public User deleteFriend(Long fromUser, Long toUser) {
        log.info("Deleting a user with an id = {}, in from friends to a user with an id = {}", toUser, fromUser);
        friendStorage.delete(fromUser, toUser);
        Event event = new Event(fromUser, EventType.FRIEND, EventOperation.REMOVE, toUser);
        eventStorage.create(event);
        log.info("Added the 'Remove from Friends' event.");
        return get(fromUser);
    }

    public List<User> getCommon(Long fromUser, Long toUser) {
        log.info("List of mutual friends");
        return friendStorage.getCommon(fromUser, toUser);
    }

    public List<User> getFriends(Long id) {
        get(id);
        log.info("List of friends of the user with id = {}", id);
        return friendStorage.get(id);
    }

    public User create(User user) {
        log.info("Creating a new user = {}", user);
        usernameValidation(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        log.info("Updating a user = {}", user);
        return userStorage.update(user);
    }

    public List<User> get() {
        log.info("List of all users");
        return userStorage.get();
    }

    public User get(Long id) {
        log.info("Getting user id = {}", id);
        return userStorage.get(id).orElseThrow(
                () -> new UserAlreadyExistException("User id = " + id + " was not found"));
    }

    private void usernameValidation(User user) {
        log.info("Username validation");
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Username equated to login: name = {}", user.getLogin());
        }
    }

    public void delete(Long id) {
        userStorage.delete(id);
        log.info("User with id {} was deleted ", id);
    }

    public List<Event> getEvents(Long id) {

        if (userStorage.get(id).isPresent()) {
            return eventStorage.get(id);
        } else {
            throw new ObjectNotFoundException("User with id " + id + " was not found");
        }
    }

    public List<Film> getRecommendations(Long id, int count) {

        List<Long> similarInterestUsers = userStorage.geSimilar(id);

        if (similarInterestUsers.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> idRecommendationFilms = filmStorage.getIdOfCommon(similarInterestUsers, id, count);

        List<Film> films = filmStorage.get(idRecommendationFilms);

        genreStorage.load(films);
        directorStorage.load(films);

        log.info("Send a list of recommended films for user id = {}", id);

        return films;
    }
}
