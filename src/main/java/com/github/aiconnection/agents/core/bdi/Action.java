package com.github.aiconnection.agents.core.bdi;

@FunctionalInterface
public interface Action {
	
	String execute(final String prompt);
	
}