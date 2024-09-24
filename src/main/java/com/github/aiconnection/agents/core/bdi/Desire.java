package com.github.aiconnection.agents.core.bdi;

import java.util.List;

public interface Desire {
	
    List<String> getConditions();
    
    Integer getPriority();
    
    String getDesireId();

	Boolean isApplicable(final BeliefBase beliefBase);
}