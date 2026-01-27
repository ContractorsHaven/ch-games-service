package org.binary.scripting.chgamesservice.service;

import org.binary.scripting.chgamesservice.dto.ScoreSubmissionRequest;
import org.binary.scripting.chgamesservice.dto.ScoreSubmittedEvent;
import reactor.core.publisher.Mono;

public interface ScoreService {

    Mono<ScoreSubmittedEvent> submitScore(ScoreSubmissionRequest request);
}
