package com.example.demo.spring.ai.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;


public class AuditTokenAdvisor implements CallAdvisor {

    private static final Logger log= LoggerFactory.getLogger(AuditTokenAdvisor.class);

    private static ChatClient openAiChatClient;

    private int order;

    public AuditTokenAdvisor(int order) {
        this.order=order;
    }


    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        ChatClientResponse response=callAdvisorChain.nextCall(chatClientRequest);
        // 2. Perform deep null checks before logging
        if (response.chatResponse() != null) {

            var usage = response.chatResponse().getMetadata().getUsage();

            usage.getCompletionTokens();
            usage.getPromptTokens();
            log.info("Tokens used - Prompt: {}, Completion: {}",
                    usage.getPromptTokens(),
                    usage.getCompletionTokens());
        } else {
            log.warn("ChatClientResponse or Metadata is null; skipping token logging.");
        }          return response;
    }

    @Override
    public String getName() {
        return "AuditTokenAdvisor";
    }

    @Override
    public int getOrder() {
        return this.order;
    }
}
