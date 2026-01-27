package org.binary.scripting.chgamesservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.binary.scripting.chgamesservice.dto.ScoreSubmissionRequest;
import org.binary.scripting.chgamesservice.dto.ScoreSubmittedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {

    private final KafkaTemplate<String, ScoreSubmittedEvent> kafkaTemplate;

    @Value("${app.kafka.topic.score-submitted}")
    private String scoreSubmittedTopic;

    @Override
    public Mono<ScoreSubmittedEvent> submitScore(ScoreSubmissionRequest request) {
        log.debug("Submitting score for game: {}, player: {}", request.getGameId(), request.getPlayerId());
        ScoreSubmittedEvent event = ScoreSubmittedEvent.builder()
                            .eventId(UUID.randomUUID())
                            .gameId(request.getGameId())
                            .playerId(request.getPlayerId())
                            .score(request.getScore())
                            .timestamp(Instant.now())
                            .build();

        log.info("Publishing score event: {} for game: {}", event.getEventId(), event.getGameId());
        return Mono.fromFuture(kafkaTemplate.send(scoreSubmittedTopic, event.getGameId().toString(), event))
                            .subscribeOn(Schedulers.boundedElastic())
                            .doOnSuccess(result -> log.debug("Score event published successfully: {}", event.getEventId()))
                            .doOnError(error -> log.error("Failed to publish score event: {}", event.getEventId(), error))
                            .thenReturn(event);
    }
}
