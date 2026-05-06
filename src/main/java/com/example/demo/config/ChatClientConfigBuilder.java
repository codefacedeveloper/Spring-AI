package com.example.demo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class ChatClientConfigBuilder {

    @Bean
    JdbcChatMemoryRepository jdbcChatMemoryRepository(DataSource dataSource){
        return JdbcChatMemoryRepository.builder().dataSource(dataSource).build();
    }

    @Bean
    public ChatClient openAiChatClient(OpenAiChatModel openAiChatModel, JdbcChatMemoryRepository jdbcChatMemoryRepository){
        ChatMemory memory=MessageWindowChatMemory.builder().chatMemoryRepository(jdbcChatMemoryRepository).maxMessages(7).build();
        Advisor advisor= MessageChatMemoryAdvisor.builder(memory).order(0).build();
        SimpleLoggerAdvisor simpleLoggerAdvisor=SimpleLoggerAdvisor.builder().order(1).build();
        return ChatClient.builder(openAiChatModel).defaultAdvisors(List.of(advisor,simpleLoggerAdvisor)).build();
    }

    @Bean
    public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel){
        return ChatClient.builder(ollamaChatModel).build();
    }
}
