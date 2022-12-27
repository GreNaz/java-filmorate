package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.helper.TestDataHelper.*;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
public class EventStorageTest {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final UserService userService;
    private final FilmService filmService;
    private final MockMvc mockMvc;

    @Test
    void getListEventsTest() throws Exception {
        filmStorage.create(FILM);
        userService.addFriend(1L, 2L);
//        filmService.createLike(1L, 1L);
        userService.getEvents(1L);
        mockMvc.perform(
                        get("/users/1/feed")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(userService.getEvents(1L).get(0).getEventId(), 2))
                .andExpect(result -> assertEquals(userService.getEvents(1L).get(0).getUserId(), 1))
                .andExpect(result -> assertEquals(userService.getEvents(1L).get(0).getEventType().getTitle(), "FRIEND"))
                .andExpect(result -> assertEquals(userService.getEvents(1L).get(0).getOperation().getTitle(), "ADD"))
                .andExpect(result -> assertEquals(userService.getEvents(1L).get(1).getEventId(), 3))
                .andExpect(result -> assertEquals(userService.getEvents(1L).get(1).getUserId(), 1))
                .andExpect(result -> assertEquals(userService.getEvents(1L).get(1).getEventType().getTitle(), "LIKE"))
                .andExpect(result -> assertEquals(userService.getEvents(1L).get(1).getOperation().getTitle(), "ADD"));
    }
}
