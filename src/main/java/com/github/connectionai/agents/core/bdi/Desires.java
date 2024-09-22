package com.github.connectionai.agents.core.bdi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import groovy.transform.builder.Builder;
import lombok.Data;

@Data
@Builder
public class Desires {

	private final List<AgentDesire> items;
	
	@JsonCreator
	public Desires(@JsonProperty("items") final List<AgentDesire> items) {
		this.items = items;
	}	
}
