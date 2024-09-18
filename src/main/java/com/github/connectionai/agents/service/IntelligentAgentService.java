package com.github.connectionai.agents.service;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IntelligentAgentService implements AgentService{
	
	private final LLMInference llmInference;
	
	private final Recognition recognition;
	
	private final Conversion conversion;

	@Autowired
	public IntelligentAgentService(final LLMInference llmInference, final Recognition recognition,
			final Conversion conversion) {
		
		this.llmInference = llmInference;
		this.recognition = recognition;
		this.conversion = conversion;
	}

	@Override
	public byte[] convert(final URL sourceFile) {
		
		return conversion.convert(sourceFile);
	}

	@Override
	public String convertVoiceToText(final byte[] voiceData) {
		
		return recognition.convertVoiceToText(voiceData);
	}

	@Override
	public String complete(final String text) {
		
		return llmInference.complete(text);
	}
}
