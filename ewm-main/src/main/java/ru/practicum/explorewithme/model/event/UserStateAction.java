package ru.practicum.explorewithme.model.event;

import java.util.Optional;

public enum UserStateAction {
    SEND_TO_REVIEW, CANCEL_REVIEW;

    public static Optional<UserStateAction> from(String state) {
        for (UserStateAction value : UserStateAction.values()) {
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
