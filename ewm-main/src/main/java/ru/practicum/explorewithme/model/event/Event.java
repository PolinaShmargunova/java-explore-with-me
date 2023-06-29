package ru.practicum.explorewithme.model.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.explorewithme.model.category.Category;
import ru.practicum.explorewithme.model.user.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    Long id;
    @Column(name = "annotation", nullable = false)
    String annotation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;
    @Column(name = "created_on", nullable = false)
    LocalDateTime createdOn;
    @Column(name = "description", nullable = false)
    String description;
    @Column(name = "event_date")
    LocalDateTime eventDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User initiator;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    Location location;
    @Column(nullable = false)
    Boolean paid;
    @Column(name = "participation_limit")
    Integer participantLimit;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    EventState state;
    @Column(name = "event_title", nullable = false)
    String title;

    @Transient
    Long confirmedRequests;

    @Transient
    Long views;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id) && Objects.equals(annotation, event.annotation) &&
                Objects.equals(category, event.category) && Objects.equals(createdOn, event.createdOn) &&
                Objects.equals(description, event.description) && Objects.equals(eventDate, event.eventDate) &&
                Objects.equals(initiator, event.initiator) && Objects.equals(location, event.location) &&
                Objects.equals(paid, event.paid) && Objects.equals(participantLimit, event.participantLimit) &&
                Objects.equals(publishedOn, event.publishedOn) &&
                Objects.equals(requestModeration, event.requestModeration) &&
                state == event.state && Objects.equals(title, event.title) &&
                Objects.equals(confirmedRequests, event.confirmedRequests) && Objects.equals(views, event.views);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, annotation, category, createdOn, description,
                eventDate, initiator, location, paid, participantLimit, publishedOn,
                requestModeration, state, title, confirmedRequests, views
        );
    }
}