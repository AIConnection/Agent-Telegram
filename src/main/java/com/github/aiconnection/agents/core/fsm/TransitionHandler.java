package com.github.aiconnection.agents.core.fsm;

import java.util.Optional;

public interface TransitionHandler {

    default State execute(final String userInput) {

        final State currentState = perceiveState(userInput).orElseThrow();

        return currentState
                .transitions()
                .stream()
                .filter(transition -> checkCondition(transition, currentState, userInput))
                .map(transition -> nextState(transition, currentState, userInput))
                .flatMap(Optional::stream)
                .findFirst()
                .orElse(currentState);
    }

    Boolean checkCondition(final Transition transition, final State currentState, final String userInput);

    Optional<State> perceiveState(final String userInput);

    Optional<State> nextState(final Transition transition, final State currentState, final String userInput);
}