package org.binary.scripting.chgamesservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.binary.scripting.chgamesservice.dto.ScoreSubmissionRequest;
import org.binary.scripting.chgamesservice.dto.ScoreSubmittedEvent;
import org.binary.scripting.chgamesservice.service.ScoreService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/v1/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<ScoreSubmittedEvent> submitScore(@RequestBody ScoreSubmissionRequest request) {
        log.info("Received score submission for game: {}, player: {}", request.getGameId(), request.getPlayerId());
        return scoreService.submitScore(request);
    }
}
