package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new ru.practicum.ViewStats(e.app, e.uri, COUNT(e.ip)) " +
            "FROM EndpointHit e " +
            "WHERE e.timestamp >= :startTime " +
            "AND e.timestamp <= :endTime " +
            "AND e.uri IN :uris " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC")
    List<ViewStats> getStats(LocalDateTime startTime, LocalDateTime endTime, List<String> uris);

    @Query("SELECT new ru.practicum.ViewStats(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndpointHit e " +
            "WHERE e.timestamp >= :startTime " +
            "AND e.timestamp <= :endTime " +
            "AND e.uri IN :uris " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC")
    List<ViewStats> getStatsUnique(LocalDateTime startTime, LocalDateTime endTime, List<String> uris);

    @Query("SELECT new ru.practicum.ViewStats(e.app, e.uri, COUNT(e.ip)) " +
            "FROM EndpointHit e " +
            "WHERE e.timestamp >= :startTime " +
            "AND e.timestamp <= :endTime " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC")
    List<ViewStats> getStatsAll(LocalDateTime startTime, LocalDateTime endTime);

}