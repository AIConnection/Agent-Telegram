package com.github.aiconnection.agents.core.bdi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Beliefs {

	private final List<AgentBelief> items;

	@JsonCreator
	public Beliefs(@JsonProperty("items") final List<AgentBelief> items) {
		this.items = items;
	}
}
