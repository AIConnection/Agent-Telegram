package com.github.connectionai.agents.core.service;

import java.net.URL;

public interface AgentService extends ConversionService, RecognitionService, LLMInference {

	default String processVoice(final URL sourceFile) {
		
		final byte[] convertData = convert(sourceFile);
		
		final String text = convertVoiceToText(convertData);
		
		return complete(text);
	}
}
