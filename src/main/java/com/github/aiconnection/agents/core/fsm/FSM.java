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
	
	private final String systemPrompt;

	private final List<State> states;
	
	@JsonIgnore
	private final Map<String, State> statesMap;

	@JsonCreator
	public FSM(@JsonProperty("systemPrompt") final String systemPrompt, @JsonProperty("states") final List<State> states) {
		
		this.systemPrompt = systemPrompt;
		
		this.states = states;
		
		this.statesMap = states
				.stream()
                .collect(Collectors.toMap(State::getName, Function.identity()));
	}

	public State get(final String target) {
		
		return this.statesMap.get(target);
	}

	public String getSystemPrompt() {
		
		return systemPrompt;
	}

	public boolean containsState(final String nextState) {

		return this.statesMap.containsKey(nextState);
	}
}
