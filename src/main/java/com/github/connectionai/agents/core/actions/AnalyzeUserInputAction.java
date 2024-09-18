package com.github.connectionai.agents.core.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.connectionai.agents.core.bdi.Action;
import com.github.connectionai.agents.core.bdi.BeliefBase;
import com.github.connectionai.agents.core.service.PLNService;

@Component
public class AnalyzeUserInputAction implements Action {

	
    private final PLNService plnService;

    @Autowired
    public AnalyzeUserInputAction(final PLNService plnService) {
        this.plnService = plnService;
    }

    @Override
    public void execute(final BeliefBase beliefBase) {
    	
    	final String userInput = (String) beliefBase.getBelief("userInput");
        
    	final var analysisResult = plnService.analyzeInput(userInput);

        beliefBase.updateBelief("analysisResult", analysisResult);
    }
}