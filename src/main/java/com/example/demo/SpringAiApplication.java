package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
public class SpringAiApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringAiApplication.class, args);
	}

	@Autowired
	ApplicationContext applicationContext;

	@Override
	public void run(String... args) throws Exception {
		String[] beanDefinitionNames=applicationContext.getBeanDefinitionNames();
	    for(String beanName : beanDefinitionNames){
			System.out.println("the bean is "+beanName);
		}
	}

//	public String readSecret() throws Exception {
//		return Files.readString(Path.of("/run/secrets/openai_api_key")).trim();
//	}
}
