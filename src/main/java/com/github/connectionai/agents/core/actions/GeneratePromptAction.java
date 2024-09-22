package com.github.connectionai.agents.core.actions;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.github.connectionai.agents.core.bdi.Action;
import com.github.connectionai.agents.core.service.PromptGeneratorService;

@Component
public class GeneratePromptAction implements Action {

    private final PromptGeneratorService promptGenerator;

    @Autowired
    public GeneratePromptAction(@Qualifier("textLLMInference") final PromptGeneratorService promptGenerator) {
        
    	this.promptGenerator = promptGenerator;
    }

    @Override
    public String execute(final String metaPrompt) {

    	return promptGenerator.generatePrompt(Arrays.asList(metaPrompt));
    }
}