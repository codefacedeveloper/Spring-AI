package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class ChatController {

    private final OpenAiChat openAiChat;

    public ChatController(OpenAiChat openAiChat) {
        this.openAiChat = openAiChat;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam("prompt") String prompt) {
        return openAiChat.getResponse(prompt);
    }
}