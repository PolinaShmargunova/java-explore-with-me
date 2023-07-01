package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.model.subscription.Subscription;
import ru.practicum.explorewithme.model.user.User;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    void deleteByUserAndSubscriber(User user, User subscriber);

    List<Subscription> findBySubscriber(User subscriber);

    @Query("select s.user.id " +
            "from Subscription as s " +
            "where s.subscriber = ?1")
    List<Long> findSubscriptionsBySubscriber(User subscriber);
}
