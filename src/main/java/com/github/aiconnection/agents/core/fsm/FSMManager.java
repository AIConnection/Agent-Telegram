package com.github.aiconnection.agents.core.fsm;

import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.github.aiconnection.agents.core.service.LLMInference;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FSMManager implements TransitionHandler {

	private static final String PROMPT_CHECK_CONDITION = "Dado o estado atual: '%s', e a condição de transição: '%s', a entrada do usuário: '%s' satisfaz a transição?";

	private static final String PROMPT_STATE_ANALYSIS = "Analisando a transição de estado:\n" +
		    "Entrada do usuário: '%s'\n" +
		    "Com base nisso, determine o próximo estado apropriado.";
	
	private static final String PROMPT_TRANSITION_STATE = "Dado o estado atual: '%s', a condição de transição: '%s', e a entrada do usuário: '%s', qual deve ser o próximo estado?";
			
	private final LLMInference llmInference;
	
	private final FSM fsm;

	@Autowired
	public FSMManager(
			@Qualifier("textLLMInference") final LLMInference llmInference, 
			final FSM fsm) {
		
	        this.fsm = fsm;
	        this.llmInference = llmInference;
	    }
	
	@Override
	public Optional<State> perceiveState(final String userInput) {
		
		final String prompt = preparePromptForStateAnalysis(userInput);
		
		final String currentState = llmInference.complete(this.fsm.getSystemPrompt(), prompt);
		
		if (fsm.containsState(currentState)) {
	    	 
	         return fsm.get(currentState);
	     } else {
	    	 
	         log.warn("m=perceptiveState, estado inválido inferido na LLM: {}", currentState);
	         
	         return Optional.ofNullable(fsm.getInitialState());
	     }
	}

	@Override
	public Boolean checkCondition(final Transition transition, final State currentState, final String userInput) {
		
		final String prompt = preparePromptForCheckConditions(transition, currentState, userInput);
        
    	final String conditionChecked = llmInference.complete(this.fsm.getSystemPrompt(), prompt);
    	
        return BooleanUtils.toBoolean(conditionChecked);
	}

	@Override
	public Optional<State> nextState(final Transition transition, final State currentState, final String userInput) {
		
		final String prompt = preparePromptForTransitionState(transition, currentState, userInput);
	     
	     final String nextState = llmInference.complete(this.fsm.getSystemPrompt(), prompt);
	     
	     if (fsm.containsState(nextState)) {
	    	 
	         return fsm.get(nextState);
	     } else {
	    	 
	         log.warn("m=nextState, estado inválido inferido na LLM: {}", nextState);
	         
	         return Optional.ofNullable(currentState);
	     }
	 }
	
	private String preparePromptForCheckConditions(final Transition transition, final State currentState, final String userInput) {
		
	    return String.format(
	        PROMPT_CHECK_CONDITION,
	        currentState.name(),
	        transition.condition(),
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
		        currentState.name(),
		        transition.condition(),
		        userInput
		    );
	}
}
