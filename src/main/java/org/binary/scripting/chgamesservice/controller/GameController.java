package org.binary.scripting.chgamesservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.binary.scripting.chgamesservice.entity.Game;
import org.binary.scripting.chgamesservice.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping
    public Flux<Game> getAllGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all games - page: {}, size: {}", page, size);
        return gameService.findAll(page, size);
    }

    @GetMapping("/active")
    public Flux<Game> getActiveGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting active games - page: {}, size: {}", page, size);
        return gameService.findActive(page, size);
    }

    @GetMapping("/genre/{genre}")
    public Flux<Game> getGamesByGenre(
            @PathVariable String genre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting games by genre: {} - page: {}, size: {}", genre, page, size);
        return gameService.findByGenre(genre, page, size);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Game>> getGameById(@PathVariable UUID id) {
        log.info("Getting game by id: {}", id);
        return gameService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Game> createGame(@RequestBody Game game) {
        log.info("Creating new game: {}", game.getName());
        return gameService.create(game);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Game>> updateGame(@PathVariable UUID id, @RequestBody Game game) {
        log.info("Updating game with id: {}", id);
        return gameService.update(id, game)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteGame(@PathVariable UUID id) {
        log.info("Deleting game with id: {}", id);
        return gameService.delete(id);
    }
}