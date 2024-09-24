package com.github.aiconnection.agents.core.service;

import java.util.List;

public interface LLMInference {
	
	String complete(final String systemPrompt, final String userPrompt, final List<String> prompts);
	String complete(final String systemPrompt, final String prompt);
	String complete(final String prompt);

}
