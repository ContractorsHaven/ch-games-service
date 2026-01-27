package org.binary.scripting.chgamesservice.controller;

import org.binary.scripting.chgamesservice.dto.ScoreSubmissionRequest;
import org.binary.scripting.chgamesservice.dto.ScoreSubmittedEvent;
import org.binary.scripting.chgamesservice.service.ScoreService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(ScoreController.class)
@ContextConfiguration(classes = {ScoreController.class, ScoreControllerTest.TestConfig.class})
class ScoreControllerTest {

    @Configuration
    static class TestConfig {
        @Bean
        public ScoreService scoreService() {
            return Mockito.mock(ScoreService.class);
        }
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ScoreService scoreService;

    @Test
    void submitScore_success_returnsAccepted() {
        UUID gameId = UUID.randomUUID();
        String playerId = "player-123";
        Long score = 1000L;

        ScoreSubmissionRequest request = ScoreSubmissionRequest.builder()
                .gameId(gameId)
                .playerId(playerId)
                .score(score)
                .build();

        UUID eventId = UUID.randomUUID();
        Instant timestamp = Instant.now();

        ScoreSubmittedEvent expectedEvent = ScoreSubmittedEvent.builder()
                .eventId(eventId)
                .gameId(gameId)
                .playerId(playerId)
                .score(score)
                .timestamp(timestamp)
                .build();

        when(scoreService.submitScore(any(ScoreSubmissionRequest.class)))
                .thenReturn(Mono.just(expectedEvent));

        webTestClient.post()
                .uri("/v1/scores")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(ScoreSubmittedEvent.class)
                .value(event -> {
                    assertThat(event.getEventId()).isEqualTo(eventId);
                    assertThat(event.getGameId()).isEqualTo(gameId);
                    assertThat(event.getPlayerId()).isEqualTo(playerId);
                    assertThat(event.getScore()).isEqualTo(score);
                    assertThat(event.getTimestamp()).isEqualTo(timestamp);
                });
    }

    @Test
    void submitScore_requestResponseMapping_correctlyMapped() {
        UUID gameId = UUID.randomUUID();
        String playerId = "player-456";
        Long score = 2500L;

        ScoreSubmissionRequest request = ScoreSubmissionRequest.builder()
                .gameId(gameId)
                .playerId(playerId)
                .score(score)
                .build();

        ScoreSubmittedEvent event = ScoreSubmittedEvent.builder()
                .eventId(UUID.randomUUID())
                .gameId(gameId)
                .playerId(playerId)
                .score(score)
                .timestamp(Instant.now())
                .build();

        when(scoreService.submitScore(any(ScoreSubmissionRequest.class)))
                .thenReturn(Mono.just(event));

        webTestClient.post()
                .uri("/v1/scores")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isAccepted()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.eventId").isNotEmpty()
                .jsonPath("$.gameId").isEqualTo(gameId.toString())
                .jsonPath("$.playerId").isEqualTo(playerId)
                .jsonPath("$.score").isEqualTo(score)
                .jsonPath("$.timestamp").isNotEmpty();
    }

    @Test
    void submitScore_serviceError_propagatesError() {
        UUID gameId = UUID.randomUUID();

        ScoreSubmissionRequest request = ScoreSubmissionRequest.builder()
                .gameId(gameId)
                .playerId("player-789")
                .score(500L)
                .build();

        when(scoreService.submitScore(any(ScoreSubmissionRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("Service unavailable")));

        webTestClient.post()
                .uri("/v1/scores")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void submitScore_invalidContentType_returnsUnsupportedMediaType() {
        webTestClient.post()
                .uri("/v1/scores")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("invalid content")
                .exchange()
                .expectStatus().isEqualTo(415);
    }

    @Test
    void submitScore_malformedJson_returnsBadRequest() {
        webTestClient.post()
                .uri("/v1/scores")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{invalid json}")
                .exchange()
                .expectStatus().is4xxClientError();
    }
}
