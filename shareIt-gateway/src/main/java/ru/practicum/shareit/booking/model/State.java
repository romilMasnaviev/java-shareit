package ru.practicum.shareit.booking.model;

import java.util.Optional;

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

    public static Optional<State> from(String stringState) {
        for (State state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}