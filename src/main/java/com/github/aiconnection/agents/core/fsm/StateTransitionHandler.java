package com.github.aiconnection.agents.core.fsm;

import java.util.Optional;

public interface StateTransitionHandler {
	
	default State execute(final String userInput) {
		
		final State currentState = perceiveState(userInput).orElseThrow();
		
		return currentState
				.getTransitions()
				.stream()
				.filter(transition->checkCondition(transition, currentState, userInput))
				.map(transition->nextState(transition, currentState, userInput))
				.findFirst()
				.orElse(currentState);
	}
	
	Optional<State> perceiveState(final String userInput);

	Boolean checkCondition(final Transition transition, final State currentState, final String userInput);

	State nextState(final Transition transition, final State currentState, final String userInput);
}