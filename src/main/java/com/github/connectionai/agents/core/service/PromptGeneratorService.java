package com.github.connectionai.agents.core.service;

import java.util.List;

public interface PromptGeneratorService {

    public String generatePrompt(final List<String> analysisResults);
}