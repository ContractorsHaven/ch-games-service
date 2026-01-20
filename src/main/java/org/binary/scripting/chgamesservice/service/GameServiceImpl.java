package org.binary.scripting.chgamesservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.binary.scripting.chgamesservice.entity.Game;
import org.binary.scripting.chgamesservice.repository.GameRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    private static final int DEFAULT_PAGE_SIZE = 10;

    @Override
    public Flux<Game> findAll(int page, int size) {
        int pageSize = size > 0 ? size : DEFAULT_PAGE_SIZE;
        return gameRepository.findAllBy(PageRequest.of(page, pageSize));
    }

    @Override
    public Flux<Game> findActive(int page, int size) {
        int pageSize = size > 0 ? size : DEFAULT_PAGE_SIZE;
        return gameRepository.findByActiveTrue(PageRequest.of(page, pageSize));
    }

    @Override
    public Flux<Game> findByGenre(String genre, int page, int size) {
        int pageSize = size > 0 ? size : DEFAULT_PAGE_SIZE;
        return gameRepository.findByGenre(genre, PageRequest.of(page, pageSize));
    }

    @Override
    public Mono<Game> findById(UUID id) {
        return gameRepository.findById(id);
    }

    @Override
    public Mono<Game> create(Game game) {
        log.debug("Creating new game: {}", game.getName());
        game.setActive(true);
        return gameRepository.save(game);
    }

    @Override
    public Mono<Game> update(UUID id, Game game) {
        log.debug("Updating game with id: {}", id);
        return gameRepository.findById(id)
                .flatMap(existingGame -> {
                    existingGame.setName(game.getName());
                    existingGame.setDescription(game.getDescription());
                    existingGame.setGenre(game.getGenre());
                    existingGame.setMinPlayers(game.getMinPlayers());
                    existingGame.setMaxPlayers(game.getMaxPlayers());
                    existingGame.setImageUrl(game.getImageUrl());
                    existingGame.setActive(game.getActive());
                    return gameRepository.save(existingGame);
                });
    }

    @Override
    public Mono<Void> delete(UUID id) {
        log.debug("Deleting game with id: {}", id);
        return gameRepository.deleteById(id);
    }
}