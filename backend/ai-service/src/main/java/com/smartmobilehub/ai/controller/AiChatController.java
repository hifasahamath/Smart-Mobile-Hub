package com.smartmobilehub.ai.controller;

import com.smartmobilehub.ai.entity.ChatMessage;
import com.smartmobilehub.ai.service.AiChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    /** Start a new chat session */
    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-User-Email", defaultValue = "anonymous") String userEmail) {

        String sessionId = body.getOrDefault("sessionId", UUID.randomUUID().toString());
        String message = body.get("message");

        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Message is required"
            ));
        }

        String response = aiChatService.chat(sessionId, userEmail, message);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "sessionId", sessionId,
                "response", response
        ));
    }

    /** Get chat history for a session */
    @GetMapping("/chat/{sessionId}")
    public ResponseEntity<Map<String, Object>> getHistory(@PathVariable String sessionId) {
        List<ChatMessage> history = aiChatService.getHistory(sessionId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "sessionId", sessionId,
                "messages", history
        ));
    }
}
