package com.github.connectionai.agents.core.bdi;

@FunctionalInterface
public interface Action {
	
	String execute(final String prompt);
	
}