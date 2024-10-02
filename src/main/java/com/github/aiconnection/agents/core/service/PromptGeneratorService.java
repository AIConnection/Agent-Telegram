package com.github.aiconnection.agents.core.service;

import java.util.List;

public interface PromptGeneratorService {

    String generatePrompt(final List<String> metaPrompts);
}