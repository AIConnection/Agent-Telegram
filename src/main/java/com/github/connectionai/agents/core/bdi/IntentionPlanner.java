package com.github.connectionai.agents.core.bdi;

import java.util.List;

public class IntentionPlanner {

    private final List<Plan> plans;

    public IntentionPlanner(final List<Plan> plans) {
    	
        this.plans = plans;
    }

    public Plan selectPlan(final Desire desire) {
    	
        return plans.stream()
                .filter(plan -> plan.getDesireId().equals(desire.getId()))
                .findFirst()
                .orElse(Plan.defaultPlan());
    }
}