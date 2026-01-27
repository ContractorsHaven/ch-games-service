package org.binary.scripting.chgamesservice.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.binary.scripting.chgamesservice.dto.ScoreSubmissionRequest;
import org.binary.scripting.chgamesservice.dto.ScoreSubmittedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoreServiceImplTest {

    @Mock
    private KafkaTemplate<String, ScoreSubmittedEvent> kafkaTemplate;

    private ScoreServiceImpl scoreService;

    private static final String TOPIC = "score-submitted";

    @BeforeEach
    void setUp() throws Exception {
        scoreService = new ScoreServiceImpl(kafkaTemplate);
        Field topicField = ScoreServiceImpl.class.getDeclaredField("scoreSubmittedTopic");
        topicField.setAccessible(true);
        topicField.set(scoreService, TOPIC);
    }

    @Test
    void submitScore_success() {
        UUID gameId = UUID.randomUUID();
        String playerId = "player-123";
        Long score = 1000L;

        ScoreSubmissionRequest request = ScoreSubmissionRequest.builder()
                .gameId(gameId)
                .playerId(playerId)
                .score(score)
                .build();

        SendResult<String, ScoreSubmittedEvent> sendResult = new SendResult<>(
                new ProducerRecord<>(TOPIC, gameId.toString(), null),
                new RecordMetadata(null, 0, 0, 0, 0, 0)
        );

        when(kafkaTemplate.send(eq(TOPIC), eq(gameId.toString()), any(ScoreSubmittedEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        StepVerifier.create(scoreService.submitScore(request))
                .assertNext(event -> {
                    assertThat(event.getEventId()).isNotNull();
                    assertThat(event.getGameId()).isEqualTo(gameId);
                    assertThat(event.getPlayerId()).isEqualTo(playerId);
                    assertThat(event.getScore()).isEqualTo(score);
                    assertThat(event.getTimestamp()).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    void submitScore_kafkaFailure() {
        UUID gameId = UUID.randomUUID();

        ScoreSubmissionRequest request = ScoreSubmissionRequest.builder()
                .gameId(gameId)
                .playerId("player-123")
                .score(1000L)
                .build();

        CompletableFuture<SendResult<String, ScoreSubmittedEvent>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka unavailable"));

        when(kafkaTemplate.send(eq(TOPIC), eq(gameId.toString()), any(ScoreSubmittedEvent.class)))
                .thenReturn(failedFuture);

        StepVerifier.create(scoreService.submitScore(request))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().contains("Kafka unavailable"))
                .verify();
    }
}
