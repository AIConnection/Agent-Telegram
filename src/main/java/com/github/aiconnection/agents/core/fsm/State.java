package com.github.aiconnection.agents.core.fsm;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class State {
	
    private final String name;
    private final List<Transition> transitions;

    @JsonCreator
  	public State(
  			@JsonProperty("name") final String name, 
  			@JsonProperty("transitions") final List<Transition> transitions) {
      	
  		this.name = name;
  		this.transitions = transitions;
  	}

    @JsonIgnore
	public String handle() {

		return transitions
				.stream()
				.map(Transition::handle)
				.collect(Collectors.joining());
	}
}