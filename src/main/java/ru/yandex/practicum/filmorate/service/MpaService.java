package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.impl.MpaDbStorage;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaDbStorage mpaDbStorage;

    public List<Mpa> get() {
        log.info("List of ratings sent");
        return mpaDbStorage.get();
    }

    public Mpa get(int id) {
        log.info("Getting genre with id = {}", id);
        return mpaDbStorage.get(id).orElseThrow(
                () -> new ObjectNotFoundException("Rating not found: " + id));
    }

}