package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import store.myproject.onlineshop.service.AlertService;

@Slf4j
@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
@Tag(name = "SSE", description = "SSE API")
public class AlertController {

    private final AlertService alertService;

    @Operation(summary = "알림 구독")
    @GetMapping(value = "/subscribe/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe(@PathVariable Long id, @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId, Authentication authentication) {
        String email = authentication.getName();
        log.info("email : {}", email);
        return alertService.subscribe(id, lastEventId);
    }

}
