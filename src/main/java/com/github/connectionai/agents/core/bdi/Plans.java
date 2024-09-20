package com.github.connectionai.agents.core.bdi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import groovy.transform.builder.Builder;
import lombok.Data;

@Data
@Builder
public class Plans {

	private final List<AgentPlan> plans;

	@JsonCreator
	public Plans(@JsonProperty("plans") final List<AgentPlan> plans) {
		this.plans = plans;
	}	
}
