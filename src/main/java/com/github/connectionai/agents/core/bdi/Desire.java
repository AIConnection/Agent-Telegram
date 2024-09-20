package com.github.connectionai.agents.core.bdi;

public interface Desire {
	
    boolean isApplicable(final BeliefBase beliefBase);
    
    int getPriority();
    
    String getId();
}