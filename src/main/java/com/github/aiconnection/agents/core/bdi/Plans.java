package com.github.aiconnection.agents.core.bdi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import groovy.transform.builder.Builder;
import lombok.Data;

@Data
@Builder
public class Plans {

	private final List<AgentPlan> items;

	@JsonCreator
	public Plans(@JsonProperty("items") final List<AgentPlan> items) {
		this.items = items;
	}	
}
