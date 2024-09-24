package com.github.aiconnection.agents.core.bdi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Beliefs {

	private final List<AgentBefief> items;

	@JsonCreator
	public Beliefs(@JsonProperty("items") final List<AgentBefief> items) {
		this.items = items;
	}
}
