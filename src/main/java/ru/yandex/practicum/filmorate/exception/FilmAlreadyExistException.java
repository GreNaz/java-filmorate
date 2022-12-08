package ru.yandex.practicum.filmorate.exception;

public class FilmAlreadyExistException extends RuntimeException{
    public FilmAlreadyExistException(String message) {
        super(message);
    }
}
