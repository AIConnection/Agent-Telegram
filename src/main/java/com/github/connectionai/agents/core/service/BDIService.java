package com.github.connectionai.agents.core.service;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.connectionai.agents.adapters.secondary.inference.TextLLMInference;
import com.github.connectionai.agents.core.bdi.ActionExecutor;
import com.github.connectionai.agents.core.bdi.Belief;
import com.github.connectionai.agents.core.bdi.BeliefBase;
import com.github.connectionai.agents.core.bdi.Desire;
import com.github.connectionai.agents.core.bdi.DesireBase;
import com.github.connectionai.agents.core.bdi.Plans;
import com.github.connectionai.agents.core.bdi.pln.PLNBase;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BDIService {

	private final PLNBase plnBase;
	private final BeliefBase beliefBase;
    private final DesireBase desireBase;
    private final Plans plans;
    private final TextLLMInference textLLMInference;

    @Autowired
    public BDIService(
    		final PLNBase plnBase,
    		final BeliefBase beliefBase, 
    		final DesireBase desireBase, 
    		final Plans plans,
    		final TextLLMInference textLLMInference,
    		final PromptGeneratorService promptGeneratorService,
    		final ActionExecutor actionExecutor) {
    	
    	this.plnBase = plnBase;
    	this.beliefBase = beliefBase;
        this.desireBase = desireBase;
        this.plans = plans;
        this.textLLMInference = textLLMInference;
    }
    
    public Pair<String, Boolean> doAction(final String userInput) {
    	
    	log.info("m=doAction, userInput={}", userInput);
    	
    	final String nplTaskResponse = perceive(userInput);
    	
    	final String analyseResult = analyse(nplTaskResponse, userInput);
		
		return Pair.of(deliberate(userInput, analyseResult), true);
    }

	@SneakyThrows
    private String perceive(final String userInput) {
    	
    	log.info("m=perceive, userInput={}", userInput);
    	
    	final String systemPrompt = plnBase
    			.handles()
    			.stream()
    			.reduce((a,b)->a.concat(b))
    			.get();

    	return textLLMInference.complete(systemPrompt, userInput);
    }
	
    private String analyse(final String plnTaskResponse, final String userInput) {

    	final String sumary = Belief.createBeliefSummary(this.beliefBase.getAllBeliefs());
    	
		return constructNarrative(sumary, userInput + ":" + plnTaskResponse);
	}

    private String constructNarrative(final String sumary, final String userInputWithPLNTaskResponse) {
    	
		return textLLMInference.complete(sumary, userInputWithPLNTaskResponse);
	}

	private String deliberate(final String userInput, final String analyseResult) {
    	
    	log.info("m=deliberate, analyseResult={}", analyseResult);

    	final List<Desire> desires = desireBase.getApplicableDesires(beliefBase);
    	
    	final StringBuilder builder = new StringBuilder();
    	builder.append(analyseResult + "nplTaskResponse:" + analyseResult + "\n");
    	builder.append("userInput:" + userInput + "\n");
    	
    	plans
    		.getItems()
    		.stream()
    		.filter(plan->desires.contains(findDesire(desires, plan.getDesireId())))
    		.forEach(plan->{
    			
    			final List<String> actions = plan.getActions();
    			actions.forEach(action->builder.append("-" + action + "\n"));
    		});
    	
    	return textLLMInference.complete(builder.toString());
    }
	
	private Desire findDesire(final List<Desire> desires, final String desireId) {

		return desires
				.stream()
				.filter(desire->desire.getDesireId().equalsIgnoreCase(desireId))
				.findFirst()
				.orElseThrow();
	}
}
