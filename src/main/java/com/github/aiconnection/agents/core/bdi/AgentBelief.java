package com.github.aiconnection.agents.core.bdi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AgentBelief implements Belief {

	private String name;
	
	private String initialValue;

	@JsonCreator
	public AgentBelief(@JsonProperty("name") final String name, @JsonProperty("initialValue") final String initialValue) {

		this.name = name;
		this.initialValue = initialValue;
	}
}