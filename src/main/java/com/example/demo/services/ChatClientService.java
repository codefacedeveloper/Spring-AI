package com.example.demo.services;

import com.example.demo.beans.Order;
import com.example.demo.spring.ai.advisor.AuditTokenAdvisor;
import com.example.demo.spring.ai.advisor.SafegaurdTokenAdvisor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Service
public class ChatClientService {

    private static final Logger log= LoggerFactory.getLogger(ChatClientService.class);

    private final ChatClient openAiChatClient;
    private final ChatClient ollamaChatClient;

    @Value("classpath:system-prompt.st")
    private Resource systemPrompt;

    @Value("classpath:user-prompt.st")
    private Resource userPrompt;


    public ChatClientService(@Qualifier("openAiChatClient") ChatClient openAiChatClient,@Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {
        this.openAiChatClient = openAiChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    public Flux<String> chatWithOpenAI(String request,String userId) {
        ChatOptions chatOptions=ChatOptions.builder().maxTokens(100).temperature(0.9).build();
        Flux<String> response = this.openAiChatClient.prompt().options(chatOptions)
                .advisors(advisorSpec -> {
                    advisorSpec.param(CONVERSATION_ID,userId);
                })
                .user(request)
                .stream().content();
        return response;
    }

    public Order returnACustomTypeResponse(String request) {
        ChatOptions chatOptions=ChatOptions.builder().maxTokens(100).temperature(0.9).build();
        Order response = this.openAiChatClient.prompt().options(chatOptions).advisors(new SimpleLoggerAdvisor(0),
                        new SafeGuardAdvisor(List.of("policy amount"),"We are not advised to reveal policy number",1),
                        new AuditTokenAdvisor(2), new SafegaurdTokenAdvisor())
                .system(systemPrompt)
                .user(userPrompt).call().entity(Order.class);
        log.info("the response ::{}",response);
        return response;
    }

    public List<String> returnListOfString(String request) {
        ChatOptions chatOptions=ChatOptions.builder().maxTokens(100).temperature(0.9).build();
        List<String> response = this.openAiChatClient.prompt().options(chatOptions).advisors(new SimpleLoggerAdvisor(0),
                        new SafeGuardAdvisor(List.of("policy amount"),"We are not advised to reveal policy number",1),
                        new AuditTokenAdvisor(2), new SafegaurdTokenAdvisor(),MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
                .system(systemPrompt)
                .user(userPrompt).call().entity(new ListOutputConverter());
        log.info("the response ::{}",response);
        return response;
    }

    public String chatWithOlamma(String request){
        return this.ollamaChatClient.prompt(request).call().content();
    }

   public String promptTemplate(String customerName, String orderedDate) throws IOException {
        return openAiChatClient.prompt().system(systemPrompt).user((promptUserSpec)->{promptUserSpec.text("A Customer named 'Gowri N' has ordered a Philips hair dryer worth 3000 Rs. She has ordered it on 16th May,2025. the order status is 'shipped'. She will recieve the order after 4 days\n" +
                "A Customer named 'Gokul Sairam S' has ordered a Xiomi mobile worth 30000 Rs. He has ordered it on 10th Jan,2025. the order status is 'Delivery In Progress'. he will recieve the order anytime\n" +
                "A Customer named 'Subhash S' has ordered a Nike Shoes worth 2000 Rs. he has ordered it on 5th Jan,2025. the order status is 'dispatched from warehouse'. he will recieve the order after 2 days\n" +
                "\n" +
                "A customer {customerName} is facing an issue in accessing a product she/he has ordered on {orderedDate}\n" +
                "He/she doesn't know the status of the product. Kindly help him/her providing status of the object").param("customerName",customerName).param("orderedDate",orderedDate);}).call().content();
   }

}
