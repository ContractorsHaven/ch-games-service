package org.binary.scripting.chgamesservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreSubmittedEvent {

    private UUID eventId;

    private UUID gameId;

    private String playerId;

    private Long score;

    private Instant timestamp;

}
