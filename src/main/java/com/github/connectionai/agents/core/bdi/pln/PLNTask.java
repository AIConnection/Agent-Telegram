package com.github.connectionai.agents.core.bdi.pln;

public interface PLNTask {
	
	String getName();
	
	String getDescription();
	
	default String handle() {
		return "-" + getName() + ":" + getDescription() + "\n";
	}

}
