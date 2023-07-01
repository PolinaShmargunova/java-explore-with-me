package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.EventState;
import ru.practicum.explorewithme.model.user.User;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    Optional<Event> findByIdAndState(Long id, EventState state);

    Optional<Event> findByIdAndInitiator(Long id, User initiator);

    Page<Event> findByInitiator(User user, Pageable pageable);
}
