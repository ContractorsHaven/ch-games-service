package org.binary.scripting.chgamesservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreSubmissionRequest {

    private UUID gameId;

    private String playerId;

    private Long score;
}
