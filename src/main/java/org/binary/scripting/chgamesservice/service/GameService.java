package org.binary.scripting.chgamesservice.service;

import org.binary.scripting.chgamesservice.entity.Game;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GameService {

    Flux<Game> findAll(int page, int size);

    Flux<Game> findActive(int page, int size);

    Flux<Game> findByGenre(String genre, int page, int size);

    Mono<Game> findById(UUID id);

    Mono<Game> create(Game game);

    Mono<Game> update(UUID id, Game game);

    Mono<Void> delete(UUID id);
}