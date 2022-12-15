package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.helper.TestDataHelper.*;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
public class UserControllerTest {

    private final ObjectMapper objectMapper;
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final MockMvc mockMvc;


    @Test
    void findAllTest() throws Exception {
        userStorage.create(USER);

        mockMvc.perform(
                        get("/users")
                )
                .andExpect(status().isOk());
    }

    @Test
    void addUser() throws Exception {

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(USER))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.birthday").value("1995-05-05"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void userUpdateTest() throws Exception {
        User user = USER_2;
        user.setEmail("nedawdw@test.ru");
        user.setLogin("new_login");
        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("nedawdw@test.ru"))
                .andExpect(jsonPath("$.login").value("new_login"));

        mockMvc.perform(
                        get("/users/2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("nedawdw@test.ru"))
                .andExpect(jsonPath("$.login").value("new_login"));

    }

    @Test
    void userNotFoundForUpdateTest() throws Exception {
        USER_2.setId(21);

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(USER_2))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserAlreadyExistException))
                .andExpect(result -> assertEquals("User with email nedawdw@test.ru already registered.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getUserByIdTest() throws Exception {

        mockMvc.perform(
                        get("/users/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.birthday").value("1995-05-05"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUserByIdNotFoundExceptionTest() throws Exception {

        mockMvc.perform(
                        get("/users/21")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserAlreadyExistException))
                .andExpect(result -> assertEquals("User id = 21 was not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void addFriendsTest() throws Exception {

        mockMvc.perform(
                        put("/users/1/friends/2")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(2, friendStorage.getFriends(1L).get(0).getId()));

    }

    @Test
    void addFriendsNotFoundTest() throws Exception {

        mockMvc.perform(
                        put("/users/9/friends/23")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserAlreadyExistException))
                .andExpect(result -> assertEquals("User id = 9 was not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void removeFriendsTest() throws Exception {
        userStorage.create(USER_2);
        friendStorage.addFriend(2L, 1L);

        mockMvc.perform(
                        delete("/users/2/friends/1")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(0, friendStorage.getFriends(2L).size()));
    }

    @Test
    void getFriendsListByIdTest() throws Exception {
        friendStorage.addFriend(1L, 2L);

        mockMvc.perform(
                        get("/users/1/friends")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(1, friendStorage.getFriends(1L).size()));
        friendStorage.deleteFriend(1L, 2L);
    }

    @Test
    void getFriendsListByIdNotFoundTest() throws Exception {

        mockMvc.perform(
                        get("/users/34/friends")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserAlreadyExistException))
                .andExpect(result -> assertEquals("User id = 34 was not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getCommonFriendsNotFoundTest() throws Exception {
        mockMvc.perform(
                        get("/users/9/friends/common/23")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserAlreadyExistException))
                .andExpect(result -> assertEquals("User id = 9 was not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
}