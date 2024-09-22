package com.github.connectionai.agents.core.bdi;

public interface Belief {

	String getName();
	String getInitialValue();
	default String handle() {
		return getName() + ":" + getInitialValue();
	}
}
