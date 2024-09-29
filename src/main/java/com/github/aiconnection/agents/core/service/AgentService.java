package com.github.aiconnection.agents.core.service;

import java.net.URL;

public interface AgentService extends ConversionService, RecognitionService, LLMInference {

	default String convertVoiceToText(final URL sourceFile) {
		
		final byte[] convertData = convert(sourceFile);
		
		return convertVoiceToText(convertData);
	}
	
	default String processText(final String history) {
		
		return complete(history);
	}
	
	String getBotToken();

	String resume(final String text);
}
