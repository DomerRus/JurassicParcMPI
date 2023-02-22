package ru.itmo.park.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(Integer id) {
        super("Пользователь с id " + id + " не найден");
    }
}
