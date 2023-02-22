package ru.itmo.park.exception;

public class DinoNotFoundException extends Exception {
    public DinoNotFoundException(Integer id) {
        super("Дино с id " + id + " не найден");
    }
}
