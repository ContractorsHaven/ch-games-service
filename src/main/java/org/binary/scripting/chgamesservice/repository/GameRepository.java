package org.binary.scripting.chgamesservice.repository;

import org.binary.scripting.chgamesservice.entity.Game;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface GameRepository extends ReactiveCrudRepository<Game, UUID> {

    Flux<Game> findAllBy(Pageable pageable);

    Flux<Game> findByActiveTrue(Pageable pageable);

    Flux<Game> findByGenre(String genre, Pageable pageable);
}