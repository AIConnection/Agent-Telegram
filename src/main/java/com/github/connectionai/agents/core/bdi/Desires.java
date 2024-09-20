package com.github.connectionai.agents.core.bdi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

import groovy.transform.builder.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor(onConstructor_={@JsonCreator})
@Data
@Builder
public class Desires {

	private final List<Desire> desires;
}
