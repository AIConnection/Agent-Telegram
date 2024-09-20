package com.github.connectionai.agents.core.bdi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IntentionPlanner {

    private final Plans plans;

    @Autowired
    public IntentionPlanner(final Plans plans) {
    	
        this.plans = plans;
    }

    public Plan selectPlan(final Desire desire) {
    	
        return plans
        		.getPlans()
        		.stream()
                .filter(plan -> plan.getDesireId().equals(desire.getId()))
                .findFirst()
                .map(plan->(Plan) plan)
                .orElse(Plan.defaultPlan());
    }
}