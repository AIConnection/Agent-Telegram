package com.github.connectionai.agents.adapters.secondary.inference;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.connectionai.agents.core.service.LLMInference;

@Component
public class TextLLMInference implements LLMInference {

	private final OpenAiChatModel openAiChatModel;
	
	public TextLLMInference(@Autowired final OpenAiChatModel openAiChatModel) {
		
		this.openAiChatModel = openAiChatModel;
	}

	@Override
	public String complete(final String text) {
		
		return openAiChatModel.call(text);
	}
}
