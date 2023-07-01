package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.request.Request;
import ru.practicum.explorewithme.model.user.User;

import java.util.Collection;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequester(User id);

    List<Request> findByEvent(Event event);

    List<Request> findByEventIdIn(Collection<Long> events);
}
