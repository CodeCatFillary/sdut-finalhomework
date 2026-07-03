package com.example.govservice.springai;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/spring-ai")
public class SpringAiController {
    private final SpringAiService springAiService;

    public SpringAiController(SpringAiService springAiService) {
        this.springAiService = springAiService;
    }

    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, Object> body) {
        String question = String.valueOf(body.getOrDefault("question", ""));
        String scene = String.valueOf(body.getOrDefault("scene", ""));
        return springAiService.chat(question, scene);
    }
}
