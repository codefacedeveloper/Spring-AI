package com.example.demo.spring.ai.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

public class SafegaurdTokenAdvisor implements CallAdvisor {

    private static final Logger log = LoggerFactory.getLogger(SafegaurdTokenAdvisor.class);
    private static int totalTokenUsage=0;

   // private static LocalDateTime tokenExpiryDay = LocalDateTime.now().plusDays(1);

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        log.info("totalTokenUsage :{}",totalTokenUsage);
        log.info("request context ::{}",chatClientRequest.context());
        if(totalTokenUsage >100){
            return buildFailureResponse("The token Usage for the day is over");
        }
//        if(LocalDateTime.now().isAfter(tokenExpiryDay)){
//            tokenExpiryDay=LocalDateTime.now().plusDays(1);
//        }
        ChatClientResponse response=callAdvisorChain.nextCall(chatClientRequest);
        var usage = response.chatResponse().getMetadata().getUsage();

        totalTokenUsage=usage.getCompletionTokens() + usage.getPromptTokens();
        return response;
    }

    private ChatClientResponse buildFailureResponse(String failureMessage) {
        return ChatClientResponse.builder()
                .chatResponse(ChatResponse.builder().generations(List.of(new Generation(AssistantMessage.builder().content(failureMessage).build())))
                        .build())
                .build();
    }

    @Override
    public String getName() {
        return "SafegaurdTokenAdvisor";
    }

    @Override
    public int getOrder() {
        return 4;
    }
}
