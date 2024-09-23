package com.github.connectionai.agents.core.bdi;

import java.util.Collection;

public interface Belief {

	String getName();
	String getInitialValue();
	
	default String handle() {
		
		return ">"
				.concat(getName())
				.concat(":")
				.concat(getInitialValue())
				.concat("\n");
	}
	
	static String createBeliefSummary(final Collection<Belief> beliefs) {
		
		return beliefs.stream().map(Belief::handle).reduce((a,b)->a.concat(b)).get();
	}
}
