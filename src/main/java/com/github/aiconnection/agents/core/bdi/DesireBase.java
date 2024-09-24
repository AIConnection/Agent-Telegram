package com.github.aiconnection.agents.core.bdi;

import java.util.List;

public abstract class DesireBase {

    private final List<Desire> desires;

    public DesireBase(final List<Desire> desires) {
    	
        this.desires = desires;
    }

    public List<Desire> getApplicableDesires(final BeliefBase beliefBase) {
    	
        return desires.stream()
                .filter(desire -> desire.isApplicable(beliefBase))
                .sorted((d1, d2) -> Integer.compare(d1.getPriority(), d2.getPriority()))
                .toList();
    }
}