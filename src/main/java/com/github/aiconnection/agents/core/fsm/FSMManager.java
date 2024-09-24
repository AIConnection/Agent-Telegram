package com.github.aiconnection.agents.core.fsm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.aiconnection.agents.core.service.LLMInference;

@Component
public class FSMManager {

	private final LLMInference llmInference;
	private final FSM fsm;

	@Autowired
	public FSMManager(final LLMInference llmInference, final FSM fsm) {
		
	        this.fsm = fsm;
	        this.llmInference = llmInference;
	    }

	public State processInput(final String userInput, final State currentState) {
		
		return currentState
				.getTransitions()
				.stream()
				.filter(transition->checkCondition(transition, currentState, userInput))
				.map(transition->nextState(transition, userInput))
				.findFirst()
				.orElse(currentState);
	}

	private Boolean checkCondition(final Transition transition, final State currentState, final String userInput) {
		
		final String prompt = preparePromptForCheclConditions(transition, currentState, userInput);
		
		return Boolean.parseBoolean(llmInference.complete(this.fsm.getSystemPrompt(), prompt));
	}

	private State nextState(final Transition transition, final String userInput) {
		
		final String prompt = preparePromptForStateAnalysis(transition.getCondition(), userInput);
		
		final String nextState = llmInference.complete(this.fsm.getSystemPrompt(), prompt);
		
        return fsm.get(nextState);
	}
	
	private String preparePromptForCheclConditions(final Transition transition, final State currentState, final String userInput) {
		
		return String.format("%s %s %s", transition.getCondition(), currentState, userInput);
	}

	private String preparePromptForStateAnalysis(final String condition, final String userInput) {
		
		return String.format("%s %s", condition, userInput);
	}
}
