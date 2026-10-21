package com.fitness.monitor.repository;

import com.fitness.monitor.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    Optional<Favorite> findByUserIdAndKindAndItemId(Long userId, String kind, String itemId);
}
