package com.github.connectionai.agents.core.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.connectionai.agents.core.bdi.Action;
import com.github.connectionai.agents.core.service.PLNService;

@Component
public class AnalyzeInputAction implements Action {

    private final PLNService plnService;

    @Autowired
    public AnalyzeInputAction(final PLNService plnService) {
    	
        this.plnService = plnService;
    }

    @Override
    public String execute(final String prompt) {
        
    	return plnService.analyze(prompt);
    }
}