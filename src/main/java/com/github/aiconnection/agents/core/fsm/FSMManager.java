package com.github.aiconnection.agents.core.fsm;

import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.aiconnection.agents.core.service.LLMInference;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FSMManager implements StateTransitionHandler{

	private static final String PROMPT_CHECK_CONDITION = "Given the current state: '%s', and the transition condition: '%s', does the user input: '%s' satisfy the transition?";

	private static final String PROMPT_STATE_ANALYSIS = "Analyzing state transition:\n" +
		    "User input: '%s'\n" +
		    "Based on this, determine the next appropriate state.";
	
	private static final String PROMPT_TRANSITION_STATE = "Given the current state: '%s', the transition condition: '%s', and the user input: '%s', what should be the next state?";
			
	private final LLMInference llmInference;
	
	private final FSM fsm;

	@Autowired
	public FSMManager(final LLMInference llmInference, final FSM fsm) {
		
	        this.fsm = fsm;
	        this.llmInference = llmInference;
	    }
	
	@Override
	public Optional<State> perceiveState(final String userInput) {
		
		final String prompt = preparePromptForStateAnalysis(userInput);
		
		final String currentState = llmInference.complete(this.fsm.getSystemPrompt(), prompt);
		
		if (fsm.containsState(currentState)) {
	    	 
	         return Optional.of(fsm.get(currentState));
	     } else {
	    	 
	         log.warn("m=perceptiveState, Invalid state received from LLM: {}", currentState);
	         
	         return Optional.empty();
	     }
	}

	@Override
	public Boolean checkCondition(final Transition transition, final State currentState, final String userInput) {
		
		final String prompt = preparePromptForCheckConditions(transition, currentState, userInput);
        
    	final String conditionChecked = llmInference.complete(this.fsm.getSystemPrompt(), prompt);
    	
        return BooleanUtils.toBoolean(conditionChecked);
	}

	@Override
	public State nextState(final Transition transition, final State currentState, final String userInput) {
		
		final String prompt = preparePromptForTransitionState(transition, currentState, userInput);
	     
	     final String nextState = llmInference.complete(this.fsm.getSystemPrompt(), prompt);
	     
	     if (fsm.containsState(nextState)) {
	    	 
	         return fsm.get(nextState);
	     } else {
	    	 
	         log.warn("m=nextState, Invalid state received from LLM: {}", nextState);
	         
	         return currentState;
	     }
	 }
	
	private String preparePromptForCheckConditions(final Transition transition, final State currentState, final String userInput) {
		
	    return String.format(
	        PROMPT_CHECK_CONDITION,
	        currentState.getName(), 
	        transition.getCondition(), 
	        userInput
	    );
	}

	private String preparePromptForStateAnalysis(final String userInput) {
		
	    return String.format(
	    		PROMPT_STATE_ANALYSIS,
	    		userInput
	    );
	}
	
	private String preparePromptForTransitionState(final Transition transition, final State currentState, final String userInput) {
	    
	    return String.format(
	    		PROMPT_TRANSITION_STATE,
		        currentState.getName(), 
		        transition.getCondition(), 
		        userInput
		    );
	}
}
