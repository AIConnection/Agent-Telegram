package com.github.aiconnection.agents.core.bdi.pln;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PLNTasks {

	private List<PLNTaskImpl> items;
	
	@JsonCreator
	public PLNTasks(@JsonProperty("items") final List<PLNTaskImpl> items) {
		this.items = items;
	}
}
