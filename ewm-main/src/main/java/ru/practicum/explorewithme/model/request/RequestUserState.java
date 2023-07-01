package ru.practicum.explorewithme.model.request;

import java.util.Optional;

public enum RequestUserState {
    CONFIRMED, REJECTED;

    public static Optional<RequestUserState> from(String state) {
        for (RequestUserState value : RequestUserState.values()) {
            if (value.name().equalsIgnoreCase(state)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return this.name();
    }
}
