package ru.practicum.explorewithme.model.request;

import java.util.Optional;

public enum RequestState {
    PENDING, CONFIRMED, CANCELED, REJECTED;

    public static Optional<RequestState> from(String state) {
        for (RequestState value : RequestState.values()) {
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
