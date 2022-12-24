package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.director.impl.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.friends.FriendStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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
        friendStorage.addFriend(fromUser, toUser);
        Event event = new Event(fromUser, EventType.FRIEND, EventOperation.ADD, toUser);
        eventStorage.addEvent(event);
        log.info("Added the 'Add to Friends' event.");
        return get(fromUser);
    }

    public User deleteFriend(Long fromUser, Long toUser) {
        log.info("Deleting a user with an id = {}, in from friends to a user with an id = {}", toUser, fromUser);
        friendStorage.deleteFriend(fromUser, toUser);
        Event event = new Event(fromUser, EventType.FRIEND, EventOperation.REMOVE, toUser);
        eventStorage.addEvent(event);
        log.info("Added the 'Remove from Friends' event.");
        return get(fromUser);
    }

    public List<User> mutualFriends(Long fromUser, Long toUser) {
        log.info("List of mutual friends");
        return friendStorage.mutualFriends(fromUser, toUser);
    }

    public List<User> getFriends(Long id) {
        get(id);
        log.info("List of friends of the user with id = {}", id);
        return friendStorage.getFriends(id);
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

    public List<User> getUsers() {
        log.info("List of all users");
        return userStorage.getUsers();
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
            log.info("Username equated to login: " + "name = " + user.getLogin());
        }
    }

    public void deleteById(Long id) {
        userStorage.deleteById(id);
        log.info("User with id {} was deleted ", id);
    }

    public List<Event> getEvents(Long id) {

        if (userStorage.get(id).isPresent()) {
            return eventStorage.events(id);
        } else {
            throw new ObjectNotFoundException("Пользователь не найден.");
        }
    }

    public List<Film> recommendations(Long id, int count) {

        //Получаем список id пользаков с общими лайками
        List<Long> similarInterestUsers = userStorage.geSimilar(id);

        if (similarInterestUsers.isEmpty()) {
            return Collections.emptyList();
        }
        //Получаем список id реккомендуемых фильмов
        List<Long> idRecommendationFilms = filmStorage.idCommonFilms(similarInterestUsers, id, count);

        List<Film> films = filmStorage.get(idRecommendationFilms).orElseThrow(() ->
                new ObjectNotFoundException("Film from  recommendation list was not found"));
        genreStorage.loadGenres(films);
        directorStorage.loadDirectors(films);
        log.info("Send a list of recommended films for user id " + id);

        return films;
    }
}
