package ru.itmo.park.exception;

public class UserDuplicateException extends Exception {
    public UserDuplicateException(String email) {
        super("Пользователь c email " + email + " уже существует");
    }
}
