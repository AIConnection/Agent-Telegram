package com.github.connectionai.agents.adapters.secondary.inference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.connectionai.agents.core.service.LLMInference;
import com.github.connectionai.agents.core.service.PLNService;
import com.github.connectionai.agents.core.service.PromptGeneratorService;

import lombok.extern.slf4j.Slf4j;

@Component("textLLMInference")
@Slf4j
public class TextLLMInference implements LLMInference, PLNService, PromptGeneratorService {

	private final OpenAiChatModel openAiChatModel;
	
	public TextLLMInference(@Autowired final OpenAiChatModel openAiChatModel) {
		
		this.openAiChatModel = openAiChatModel;
	}
	
	public String complete(final Prompt prompt) {
		
		final ChatResponse chatResonse = openAiChatModel.call(prompt);
		
		return chatResonse
				.getResult()
				.getOutput()
				.getContent();
	}

	@Override
	public String complete(final String systemPrompt, final String userPrompt) {
		
		log.info("m=complete, systemPrompt={}, userPrompt={}", systemPrompt, userPrompt);
		
		return complete(new Prompt(Arrays.asList(new SystemMessage(systemPrompt), new UserMessage(userPrompt))));
	}
	
	@Override
	public String complete(final String systemPrompt, final String userPrompt, final List<String> prompts) {
		
		log.info("m=complete, systemPrompt={}, userPrompt={}, prompts={}", systemPrompt, userPrompt, Arrays.toString(prompts.toArray()));
		
		final StringBuilder builder = new StringBuilder();
		prompts.forEach(builder::append);
		
		final ArrayList<Message> bdiMessages = new ArrayList<>();

		bdiMessages.add(new SystemMessage(systemPrompt));
		bdiMessages.add(new UserMessage(builder.toString()));
				
		return complete(new Prompt(bdiMessages));
	}

	@Override
	public String complete(final String userPrompt) {
		
		log.info("m=complete, userPrompt={}", userPrompt);
		
		final Prompt prompt = new Prompt(userPrompt);
		
		return complete(prompt);
	}

	@Override
	public String generatePrompt(final List<String> metaPrompts) {
		
		final List<Message> messages = metaPrompts
				.stream()
				.map(text->(Message) new AssistantMessage(text))
				.toList();
		
		messages.add(new SystemMessage("crie prompts."));
		
		final Prompt prompt = new Prompt(messages);
		
		return complete(prompt);
	}
}
