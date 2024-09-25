package com.github.aiconnection.agents.core.fsm;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.aiconnection.agents.core.bdi.pln.PLNBase;

import lombok.Data;

@Data
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
	public String generateSystemPrompt(final PLNBase plnBase) {

		return plnBase
        		.handles()
        		.stream()
        		.reduce(String::concat)
        		.orElseThrow(() -> new RuntimeException("No system prompt available"));
	}
}