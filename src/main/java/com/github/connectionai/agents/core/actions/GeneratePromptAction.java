package com.github.connectionai.agents.core.actions;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.github.connectionai.agents.core.bdi.Action;
import com.github.connectionai.agents.core.service.LLMInference;
import com.github.connectionai.agents.core.service.PromptGeneratorService;

@Component
public class GeneratePromptAction implements Action {

    private final PromptGeneratorService promptGenerator;
    
    private final LLMInference llmInference;

    @Autowired
    public GeneratePromptAction(@Qualifier("textLLMInference") final PromptGeneratorService promptGenerator, @Qualifier("textLLMInference") final LLMInference llmInference) {
        
    	this.promptGenerator = promptGenerator;
        this.llmInference = llmInference;
    }

    @Override
    public String execute(final String metaPrompt) {
    	
    	final String prompt = promptGenerator.generatePrompt(Arrays.asList(metaPrompt));

    	return llmInference.complete(prompt);
    }
}