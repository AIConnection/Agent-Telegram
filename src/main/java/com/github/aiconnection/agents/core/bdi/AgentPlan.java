package com.github.aiconnection.agents.core.bdi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import groovy.transform.builder.Builder;

@Builder
public class AgentPlan implements Plan{
	
	private final String id;
	private final String desireId;
	private final List<String> actions;
	
	@JsonCreator
	public AgentPlan(
			@JsonProperty("id") final String id,
			@JsonProperty("desireId") final String desireId, 
			@JsonProperty("actions") final List<String> actions) {
		
		this.id = id;
		this.desireId = desireId;
		this.actions = actions;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String getDesireId() {
		return desireId;
	}

	@Override
	public List<String> getActions() {
		return actions;
	}
}
