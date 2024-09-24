package com.github.aiconnection.agents.core.actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.github.aiconnection.agents.core.bdi.Action;
import com.github.aiconnection.agents.core.service.LLMInference;

@Component
public class ProvideResponseAction implements Action {
	
	private final LLMInference llmInference;
	
	@Autowired
	public ProvideResponseAction(@Qualifier("textLLMInference") final LLMInference llmInference) {
		this.llmInference = llmInference;
	}

    @Override
    public String execute(final String text) {

    	return llmInference.complete(text);
    	  
    }
}