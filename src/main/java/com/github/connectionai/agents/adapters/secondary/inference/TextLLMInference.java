package com.github.connectionai.agents.adapters.secondary.inference;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

@Component("textLLMInference")
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
	public String complete(final String text) {
		
		return complete(new Prompt(Arrays.asList(new UserMessage(text))));
	}

	@Override
	public String analyze(final String prompt) {
		
		return generatePrompt(Arrays.asList(prompt));
	}

	@Override
	public String generatePrompt(final List<String> texts) {
		
		final List<Message> messages = texts
				.stream()
				.map(text->(Message) new AssistantMessage(text))
				.collect(Collectors.toList());
		
		final SystemMessage systemMessage = new SystemMessage("gere um prompt rico e detalhado.");
		
		messages.add(systemMessage);
		
		final Prompt prompt = new Prompt(messages);
		
		return complete(prompt);
	}
}
