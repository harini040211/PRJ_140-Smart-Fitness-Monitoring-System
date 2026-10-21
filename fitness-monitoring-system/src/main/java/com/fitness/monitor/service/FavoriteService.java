package com.fitness.monitor.service;

import com.fitness.monitor.model.Favorite;
import com.fitness.monitor.repository.FavoriteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FavoriteService {

    private final FavoriteRepository repository;
    private final CatalogService catalog;

    public FavoriteService(FavoriteRepository repository, CatalogService catalog) {
        this.repository = repository;
        this.catalog = catalog;
    }

    public List<Favorite> list(Long userId) {
        return repository.findByUserId(userId);
    }

    public boolean toggle(Long userId, String kind, String itemId) {
        boolean known = switch (kind) {
            case "exercise" -> catalog.isExercise(itemId);
            case "food" -> catalog.isFood(itemId);
            default -> false;
        };
        if (!known) {
            throw new IllegalArgumentException("That item could not be found");
        }
        Optional<Favorite> existing = repository.findByUserIdAndKindAndItemId(userId, kind, itemId);
        if (existing.isPresent()) {
            repository.delete(existing.get());
            return false;
        }
        repository.save(new Favorite(userId, kind, itemId));
        return true;
    }
}
