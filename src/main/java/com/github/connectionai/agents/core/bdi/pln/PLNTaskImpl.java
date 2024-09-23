package com.github.connectionai.agents.core.bdi.pln;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PLNTaskImpl implements PLNTask{
	
	private String name;
	
	private String description;

	@JsonCreator
	public PLNTaskImpl(@JsonProperty("name") final String name, @JsonProperty("description") final String description) {

		this.name = name;
		this.description = description;
	}
}
