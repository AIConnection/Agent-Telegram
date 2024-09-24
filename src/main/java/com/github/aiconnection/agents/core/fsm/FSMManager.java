package com.github.aiconnection.agents.core.fsm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.aiconnection.agents.core.service.LLMInference;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FSMManager {

	private static final String PROMPT_CHECK_CONDITION = "Given the current state: '%s', and the transition condition: '%s', does the user input: '%s' satisfy the transition?";

	private static final String PROMPT_STATE_ANALISYS = "Analyzing state transition:\n" +
			"Current state: '%s\n'" +
			"Condition: '%s'\n" +
			"User input: '%s'\n" +
			"Based on this, determine the next appropriate state.";
			
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
				.map(transition->nextState(transition, currentState, userInput))
				.findFirst()
				.orElse(currentState);
	}

	private Boolean checkCondition(final Transition transition, final State currentState, final String userInput) {
	    try {
	        
	    	final String prompt = preparePromptForCheckConditions(transition, currentState, userInput);
	        
	        return Boolean.parseBoolean(llmInference.complete(this.fsm.getSystemPrompt(), prompt));
	    } catch (final Exception e) {

	    	log.error("m=checkCondition, transition={}, currentState={}, userInput={}", transition, currentState, userInput, e);
	    	
	        return false;
	    }
	}

	private State nextState(final Transition transition, final State currentState, final String userInput) {
		
		final String prompt = preparePromptForStateAnalysis(transition, currentState, userInput);
		
		final String nextState = llmInference.complete(this.fsm.getSystemPrompt(), prompt);
		
        return fsm.get(nextState);
	}
	
	private String preparePromptForCheckConditions(final Transition transition, final State currentState, final String userInput) {
	    return String.format(
	        PROMPT_CHECK_CONDITION,
	        currentState.getName(), 
	        transition.getCondition(), 
	        userInput
	    );
	}

	private String preparePromptForStateAnalysis(final Transition transition, final State currentState, final String userInput) {
		
	    return String.format(
	        PROMPT_STATE_ANALISYS,
	        currentState.getName(), 
	        transition.getCondition(), 
	        userInput
	    );
	}
}
