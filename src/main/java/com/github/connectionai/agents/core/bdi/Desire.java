package com.github.connectionai.agents.core.bdi;

public interface Desire {
	
    boolean isApplicable(BeliefBase beliefBase);
    
    int getPriority();
    
    String getId();
}