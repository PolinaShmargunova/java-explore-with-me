package ru.practicum.explorewithme.model.event;

import java.util.Optional;

public enum AdminStateAction {
    PUBLISH_EVENT, REJECT_EVENT;

    public static Optional<AdminStateAction> from(String state) {
        for (AdminStateAction value : AdminStateAction.values()) {
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
