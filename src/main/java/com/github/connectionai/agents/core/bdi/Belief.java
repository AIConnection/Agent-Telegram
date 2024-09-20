package com.github.connectionai.agents.core.bdi;

public interface Belief {

	String getName();
	String getType();
	String getInitialValue();
	default String handle() {
		return getType() + "." + getName() + ":" + getInitialValue();
	}
}
