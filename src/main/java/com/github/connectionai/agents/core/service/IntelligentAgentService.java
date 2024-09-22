package com.github.connectionai.agents.core.service;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service("intelligentAgentService")
@Slf4j
public class IntelligentAgentService implements AgentService{
	
	private final LLMInference llmInference;
	
	private final RecognitionService recognition;
	
	private final ConversionService conversion;
	
	private final BDIService bdiService;
	
	private final String botToken;

	public IntelligentAgentService(
			@Value("${botToken}") final String botToken,
			@Autowired final LLMInference llmInference,
			@Autowired final RecognitionService recognition,
			@Autowired final ConversionService conversion,
			@Autowired final BDIService bdiService) {
		
		this.botToken = botToken;
		this.llmInference = llmInference;
		this.recognition = recognition;
		this.conversion = conversion;
		this.bdiService = bdiService;
	}
	
	@Override
	public String getBotToken() {
		
		return botToken;
	}

	@Override
	public byte[] convert(final URL sourceFile) {
		
		log.info("m=convert, sourceFile={}", sourceFile);
		
		return conversion.convert(sourceFile);
	}

	@Override
	public String convertVoiceToText(final byte[] voiceData) {
		
		log.info("m=convertVoiceToText, voiceData.length={}", voiceData.length);
		
		return recognition.convertVoiceToText(voiceData);
	}

	@Override
	public String complete(final String userPrompt) {
		
		log.info("m=complete, userPrompt={}", userPrompt);
		
		final Pair<String, Boolean> result = bdiService.doAction(userPrompt);
		
		return result.getKey();
	}

	@Override
	public String complete(final String systemPrompt, final String userPrompt, final List<String> prompts) {
		
		log.info("m=complete, systemPrompt={}, userPrompt={}, prompts={}", systemPrompt, userPrompt, Arrays.toString(prompts.toArray()));
		
		final StringBuilder builder = new StringBuilder();
		
		prompts.forEach(prompt -> builder.append(bdiService.doAction(prompt)));
		
		return llmInference.complete(systemPrompt, userPrompt, prompts);
	}

	@Override
	public String complete(final String systemPrompt, final String prompt) {
		
		log.info("m=complete, systemPrompt={}, prompt={}", systemPrompt, prompt);
		
		final Pair<String, Boolean> result = bdiService.doAction(prompt);
		
		return result.getValue().booleanValue() ? llmInference.complete(systemPrompt, result.getKey()) : result.getKey();
	}
}
