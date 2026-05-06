package com.example.demo;

import com.example.demo.beans.Order;
import com.example.demo.services.ChatClientService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/ai")
public class ChatController {



    @Autowired
    private ChatClientService chatClientService;


//    public ChatController(OpenAiChat openAiChat) {
//        this.openAiChat = openAiChat;
//    }

    @GetMapping("openai/chat")
    public Flux<String> openAIchat(@RequestParam("prompt") String prompt, @RequestParam("userId") String userId) {
        return chatClientService.chatWithOpenAI(prompt,userId);
    }

    @GetMapping("order/details")
    public List<String> returnOrderResponse(@RequestParam("prompt") String prompt) {
        return chatClientService.returnListOfString(prompt);
    }

    @GetMapping("ollama/chat")
    public String ollamaChat(@RequestParam("prompt") String prompt) {
        return chatClientService.chatWithOlamma(prompt);
    }

    @GetMapping("guide/me")
    public String guideMe(@RequestParam("customerName")String customerName, @RequestParam("orderedDate")String orderedDate) throws IOException {
        return chatClientService.promptTemplate(customerName,orderedDate);
    }
}