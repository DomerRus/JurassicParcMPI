package ru.itmo.park.exception;

public class UserDuplicateException extends Exception {
    public UserDuplicateException(String errorMessage) {
        super(errorMessage);
    }
}
