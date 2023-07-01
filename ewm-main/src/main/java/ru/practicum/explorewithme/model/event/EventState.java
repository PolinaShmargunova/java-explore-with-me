package ru.practicum.explorewithme.model.event;

import java.util.Optional;

public enum EventState {
    PENDING, PUBLISHED, CANCELED;

    public static Optional<EventState> from(String state) {
        for (EventState value : EventState.values()) {
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
