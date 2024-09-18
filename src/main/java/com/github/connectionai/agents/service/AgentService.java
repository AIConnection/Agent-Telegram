package com.github.connectionai.agents.service;

import java.net.URL;

public interface AgentService extends Conversion, Recognition, LLMInference {

	default String processVoice(final URL sourceFile) {
		
		final byte[] convertData = convert(sourceFile);
		
		final String text = convertVoiceToText(convertData);
		
		return complete(text);
	}
}
