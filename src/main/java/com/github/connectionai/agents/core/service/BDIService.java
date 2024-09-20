package com.github.connectionai.agents.core.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.github.connectionai.agents.core.bdi.Action;
import com.github.connectionai.agents.core.bdi.ActionExecutor;
import com.github.connectionai.agents.core.bdi.BeliefBase;
import com.github.connectionai.agents.core.bdi.Desire;
import com.github.connectionai.agents.core.bdi.DesireBase;
import com.github.connectionai.agents.core.bdi.IntentionPlanner;
import com.github.connectionai.agents.core.bdi.Plan;

@Service
public class BDIService {

	private final ApplicationContext applicationContext;
	private final BeliefBase beliefBase;
    private final DesireBase desireBase;
    private final PromptGeneratorService promptGeneratorService;
    private final IntentionPlanner intentionPlanner;
    private final ActionExecutor actionExecutor;

    @Autowired
    public BDIService(final ApplicationContext applicationContext, final BeliefBase beliefBase, final DesireBase desireBase, final PromptGeneratorService promptGeneratorService, final IntentionPlanner intentionPlanner, final ActionExecutor actionExecutor) {
        this.applicationContext = applicationContext;
    	this.beliefBase = beliefBase;
        this.desireBase = desireBase;
        this.promptGeneratorService = promptGeneratorService;
        this.intentionPlanner = intentionPlanner;
        this.actionExecutor = actionExecutor;
    }
    
    public void doAction(final String userInput) {
    	final String prompt = perceive(userInput);
    	deliberate(prompt);
    }

    private String perceive(final String userInput) {
    	
    	final List<String> metaPrompts = beliefBase
    			.getAllBeliefs()
    			.stream()
    			.map(belief->belief.handle())
    			.collect(Collectors.toList());
    	
    	metaPrompts.add(userInput);

    	return promptGeneratorService.generatePrompt(metaPrompts);
    	
    }

    private void deliberate(final String prompt) {
    	
    	final List<Desire> applicableDesires = desireBase.getApplicableDesires(beliefBase);
        
    	if (!applicableDesires.isEmpty()) {

        	final Desire topDesire = applicableDesires.get(0);
        	
        	final Plan plan = intentionPlanner.selectPlan(topDesire);
        	
        	actionExecutor.executeActions(getActions(plan.getActions()), prompt);
        }
    }

	private List<Action> getActions(final List<String> actions) {

		return actions
				.stream()
				.map(action->applicationContext.getBean(action, Action.class))
				.toList();
	}
}
