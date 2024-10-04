package com.github.aiconnection.agents.core.bdi;

import lombok.Getter;

import java.util.Comparator;
import java.util.List;

@Getter
public abstract class DesireBase {

    private final List<Desire> desires;

    public DesireBase(final List<Desire> desires) {
    	
        this.desires = desires;
    }

    public List<Desire> getApplicableDesires(final BeliefBase beliefBase) {
    	
        return desires.stream()
                .filter(desire -> desire.isApplicable(beliefBase))
                .sorted(Comparator.comparingInt(Desire::getPriority))
                .toList();
    }
}