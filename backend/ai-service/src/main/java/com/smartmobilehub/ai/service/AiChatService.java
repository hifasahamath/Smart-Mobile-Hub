package com.smartmobilehub.ai.service;

import com.smartmobilehub.ai.entity.ChatMessage;
import com.smartmobilehub.ai.repository.ChatMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI-powered shopping assistant using Google Gemini.
 * Answers product questions, compares phones, and helps customers choose.
 */
@Service
public class AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);

    private final ChatMessageRepository chatMessageRepository;
    private final RestTemplate restTemplate;

    @Value("${ai.gemini.api-key:}")
    private String geminiApiKey;

    @Value("${ai.gemini.model:gemini-2.0-flash}")
    private String geminiModel;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    private static final String SYSTEM_PROMPT = """
        You are a helpful mobile phone shopping assistant for Smart Mobile Hub, a premium mobile phone store.
        
        Your responsibilities:
        - Help customers choose the right phone based on their needs and budget
        - Compare different phone models (specs, camera, battery, performance)
        - Answer technical questions about phones
        - Provide honest recommendations
        - Mention prices when you know them
        
        Guidelines:
        - Be concise but informative
        - Always be friendly and professional
        - If you don't know a specific price or availability, say so
        - Never recommend products from other stores
        - Focus on smartphones, tablets, and accessories
        """;

    public AiChatService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.restTemplate = new RestTemplate();
    }

    public String chat(String sessionId, String userEmail, String userMessage) {
        // Save user message
        ChatMessage userMsg = new ChatMessage();
        userMsg.setSessionId(sessionId);
        userMsg.setUserEmail(userEmail);
        userMsg.setRole(ChatMessage.Role.USER);
        userMsg.setContent(userMessage);
        chatMessageRepository.save(userMsg);

        // Get conversation history
        List<ChatMessage> history = chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);

        String assistantResponse;
        if (geminiApiKey != null && !geminiApiKey.isBlank()) {
            assistantResponse = callGemini(history);
        } else {
            // Fallback: rule-based response when no API key is configured
            assistantResponse = generateFallbackResponse(userMessage);
        }

        // Save assistant response
        ChatMessage assistantMsg = new ChatMessage();
        assistantMsg.setSessionId(sessionId);
        assistantMsg.setUserEmail(userEmail);
        assistantMsg.setRole(ChatMessage.Role.ASSISTANT);
        assistantMsg.setContent(assistantResponse);
        chatMessageRepository.save(assistantMsg);

        return assistantResponse;
    }

    public List<ChatMessage> getHistory(String sessionId) {
        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    private String callGemini(List<ChatMessage> history) {
        try {
            String url = String.format(GEMINI_API_URL, geminiModel, geminiApiKey);

            // Build Gemini request
            List<Map<String, Object>> contents = new ArrayList<>();

            // System instruction
            contents.add(Map.of(
                    "role", "user",
                    "parts", List.of(Map.of("text", SYSTEM_PROMPT))
            ));
            contents.add(Map.of(
                    "role", "model",
                    "parts", List.of(Map.of("text", "I'm ready to help you find the perfect mobile phone!"))
            ));

            // Conversation history
            for (ChatMessage msg : history) {
                String role = msg.getRole() == ChatMessage.Role.USER ? "user" : "model";
                contents.add(Map.of(
                        "role", role,
                        "parts", List.of(Map.of("text", msg.getContent()))
                ));
            }

            Map<String, Object> requestBody = Map.of("contents", contents);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            // Extract text from Gemini response
            Map body = response.getBody();
            if (body != null) {
                List<Map> candidates = (List<Map>) body.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map content = (Map) candidates.get(0).get("content");
                    List<Map> parts = (List<Map>) content.get("parts");
                    if (parts != null && !parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }
            return "I'm having trouble processing your request. Please try again.";
        } catch (Exception e) {
            log.error("Gemini API call failed: {}", e.getMessage());
            return generateFallbackResponse(
                    chatMessageRepository.findBySessionIdOrderByCreatedAtAsc("")
                            .stream().map(ChatMessage::getContent).collect(Collectors.joining()));
        }
    }

    private String generateFallbackResponse(String userMessage) {
        String lower = userMessage.toLowerCase();

        if (lower.contains("iphone") || lower.contains("apple")) {
            return "We carry the latest iPhones including the iPhone 15 Pro Max with the A17 Pro chip, " +
                    "titanium design, and 48MP camera. Starting from $1,199. " +
                    "Would you like to know about specific storage options or colors?";
        }
        if (lower.contains("samsung") || lower.contains("galaxy")) {
            return "The Samsung Galaxy S24 Ultra is our top Samsung pick — featuring a 200MP camera, " +
                    "S Pen, and Galaxy AI features. Starting from $1,299.99. " +
                    "We also carry mid-range Samsung options. What's your budget?";
        }
        if (lower.contains("pixel") || lower.contains("google")) {
            return "The Google Pixel 8 Pro offers the best camera AI processing with Tensor G3, " +
                    "7 years of updates, and Magic Eraser. Starting from $999. " +
                    "It's great for photography enthusiasts!";
        }
        if (lower.contains("budget") || lower.contains("cheap") || lower.contains("affordable")) {
            return "For great value, check out the Xiaomi Redmi Note 13 Pro ($299.99) with a " +
                    "200MP camera and 120Hz AMOLED display. The OnePlus 12 ($799.99) is also " +
                    "excellent if you want flagship specs at a lower price.";
        }
        if (lower.contains("compare")) {
            return "I'd be happy to compare phones! Which two models are you considering? " +
                    "I can compare specs like camera, battery, processor, and price.";
        }
        if (lower.contains("camera")) {
            return "For the best camera phones, I'd recommend:\n" +
                    "1. Samsung Galaxy S24 Ultra (200MP) — best zoom\n" +
                    "2. iPhone 15 Pro Max (48MP) — best video\n" +
                    "3. Google Pixel 8 Pro (50MP) — best AI processing\n" +
                    "Which matters most to you?";
        }

        return "I'm your Smart Mobile Hub shopping assistant! I can help you:\n" +
                "• Find the perfect phone for your needs\n" +
                "• Compare different models\n" +
                "• Answer technical questions\n" +
                "• Check prices and availability\n\n" +
                "What are you looking for in a phone?";
    }
}
