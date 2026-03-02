package com.example.demo;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class OpenAiChat {

    private static final Logger log = LoggerFactory.getLogger(OpenAiChat.class);

    @Autowired
    Environment env;

//    @PostConstruct
//    public void getValue(){
//        log.info("the value of apikey ::{}",env.getRequiredProperty(""));
//    }

    private final ChatModel chatModel;

    public OpenAiChat(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String getResponse(String userPrompt) {

        Prompt prompt = new Prompt(new UserMessage(userPrompt));

        return Objects.requireNonNull(chatModel.call(prompt)
                        .getResult())
                .getOutput().toString();
    }
}
