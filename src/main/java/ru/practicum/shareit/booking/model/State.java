package ru.practicum.shareit.booking.model;

public enum State {
    ALL, PAST, WAITING, REJECTED, CURRENT, FUTURE;

    public static boolean isValidValue(String value) {
        for (State enumValue : State.values()) {
            if (enumValue.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
}