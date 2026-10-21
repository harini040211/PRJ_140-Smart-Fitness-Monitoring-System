package com.fitness.monitor.controller;

import com.fitness.monitor.dto.ApiResponse;
import com.fitness.monitor.model.Favorite;
import com.fitness.monitor.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Favorite>>> list(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("Favorites loaded", favoriteService.list(userId)));
    }

    @PostMapping("/{kind}/{itemId}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> toggle(@RequestAttribute("userId") Long userId,
                                                                    @PathVariable String kind,
                                                                    @PathVariable String itemId) {
        boolean saved = favoriteService.toggle(userId, kind, itemId);
        return ResponseEntity.ok(ApiResponse.ok(saved ? "Added to favorites" : "Removed from favorites",
                Map.of("favorite", saved)));
    }
}
