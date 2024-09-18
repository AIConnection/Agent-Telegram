package br.senac.pos.ia.agents.adapters.secundary.inference;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.senac.pos.ia.agents.service.LLMInference;

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
