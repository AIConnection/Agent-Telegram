package com.github.aiconnection.agents.core.fsm;

import java.util.List;

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

	public List<State> processInput(final String userInput, final State currentState) {
		
		return currentState
				.getTransitions()
				.stream()
				.map(transition->nextState(transition, userInput))
				.toList();
	}

	private State nextState(final Transition transition, final String userInput) {
		
		final String prompt = preparePrompt(transition.getCondition(), userInput);
		
		final String nextState = llmInference.complete(this.fsm.getSystemPrompt(), prompt);
		
        return fsm.get(nextState);
	}

	private String preparePrompt(final String condition, final String userInput) {
		
		return String.format("%s %s", condition, userInput);
	}
}
