package com.github.aiconnection.agents.core.fsm;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.aiconnection.agents.core.bdi.pln.PLNBase;

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
	public String generateSystemPrompt(final PLNBase plnBase) {

		return plnBase
        		.handles()
        		.stream()
        		.map(handle->String.format(handle, name))
        		.map(handle->String.format(handle, transitions
							        				.stream()
							        				.map(Transition::handle)))
        		.reduce(String::concat)
        		.orElseThrow(() -> new RuntimeException("No system prompt available"));
	}
}