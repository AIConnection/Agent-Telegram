package com.github.connectionai.agents.core.service;

import java.util.List;

import com.github.connectionai.agents.core.bdi.ActionExecutor;
import com.github.connectionai.agents.core.bdi.BeliefBase;
import com.github.connectionai.agents.core.bdi.Desire;
import com.github.connectionai.agents.core.bdi.DesireBase;
import com.github.connectionai.agents.core.bdi.IntentionPlanner;
import com.github.connectionai.agents.core.bdi.Plan;

public class BDIService {

	private final BeliefBase beliefBase;
    private final DesireBase desireBase;
    private final IntentionPlanner intentionPlanner;
    private final ActionExecutor actionExecutor;

    public BDIService(final BeliefBase beliefBase, final DesireBase desireBase, final IntentionPlanner intentionPlanner, final ActionExecutor actionExecutor) {
        this.beliefBase = beliefBase;
        this.desireBase = desireBase;
        this.intentionPlanner = intentionPlanner;
        this.actionExecutor = actionExecutor;
    }

    public void perceive(final String userInput) {
        beliefBase.updateBelief("userInput", userInput);
    }

    public void deliberate() {
    	
    	final List<Desire> applicableDesires = desireBase.getApplicableDesires(beliefBase);
        if (!applicableDesires.isEmpty()) {

        	final Desire topDesire = applicableDesires.get(0);
        	
        	final Plan plan = intentionPlanner.selectPlan(topDesire);
        	
            if (plan != null) {
            	
                actionExecutor.executeActions(plan.getActions(), beliefBase);
            }
        }
    }
}
