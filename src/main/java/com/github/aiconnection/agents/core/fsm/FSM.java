package com.github.aiconnection.agents.core.fsm;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FSM {
	
	private final State initialState;
	private final List<State> states;
	
	@JsonIgnore
	private final Map<String, State> statesMap;

	@JsonCreator
	public FSM(
			@JsonProperty("initialState") final String initialState,
			@JsonProperty("states") final List<State> states) {
		
		this.states = states;
		
		this.statesMap = states
				.stream()
                .collect(Collectors.toMap(State::getName, Function.identity()));
		
		this.initialState = this.statesMap.get(initialState);
	}

	public State get(final String target) {
		
		return this.statesMap.get(target);
	}

	public String getSystemPrompt() {
		
		return states
				.stream()
				.map(State::handle)
				.collect(Collectors.joining());
	}

	public boolean containsState(final String nextState) {

		return this.statesMap.containsKey(nextState);
	}
}
