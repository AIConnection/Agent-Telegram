package com.github.connectionai.agents.core.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.connectionai.agents.core.bdi.Action;
import com.github.connectionai.agents.core.bdi.BeliefBase;
import com.github.connectionai.agents.core.service.LLMInference;
import com.github.connectionai.agents.core.service.PromptGeneratorService;

@Component
public class GeneratePromptAction implements Action {

    private final PromptGeneratorService promptGenerator;
    
    private final LLMInference llmInference;

    @Autowired
    public GeneratePromptAction(final PromptGeneratorService promptGenerator, final LLMInference llmInference) {
        
    	this.promptGenerator = promptGenerator;
        this.llmInference = llmInference;
    }

    @Override
    public void execute(final BeliefBase beliefBase) {

    	final Object analysisResult = beliefBase.getBelief("analysisResult");

    	final String prompt = promptGenerator.generatePrompt(analysisResult);

    	final String response = llmInference.complete(prompt);

        beliefBase.updateBelief("response", response);
    }
}