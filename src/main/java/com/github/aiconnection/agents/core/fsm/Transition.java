package com.github.aiconnection.agents.core.fsm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Transition {
	
    private final String trigger;
    private final String target;
    private final String condition;
    
    @JsonCreator
	public Transition(
			@JsonProperty("trigger") final String trigger, 
			@JsonProperty("target") final String target, 
			@JsonProperty("condition") final String condition) {
    	
		this.trigger = trigger;
		this.target = target;
		this.condition = condition;
	}
}