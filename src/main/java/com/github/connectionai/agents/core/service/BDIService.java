package com.github.connectionai.agents.core.service;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.connectionai.agents.adapters.secondary.inference.TextLLMInference;
import com.github.connectionai.agents.core.bdi.ActionExecutor;
import com.github.connectionai.agents.core.bdi.Belief;
import com.github.connectionai.agents.core.bdi.BeliefBase;
import com.github.connectionai.agents.core.bdi.Desire;
import com.github.connectionai.agents.core.bdi.DesireBase;
import com.github.connectionai.agents.core.bdi.Plans;
import com.github.connectionai.agents.core.bdi.pln.TaskResponse;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BDIService {

	private final BeliefBase beliefBase;
    private final DesireBase desireBase;
    private final Plans plans;
    private final TextLLMInference textLLMInference;
    private final ObjectMapper objectMapper;

    @Autowired
    public BDIService(
    		final BeliefBase beliefBase, 
    		final DesireBase desireBase, 
    		final Plans plans,
    		final TextLLMInference textLLMInference,
    		final PromptGeneratorService promptGeneratorService,
    		final ActionExecutor actionExecutor, 
    		final ObjectMapper objectMapper) {
    	
    	this.beliefBase = beliefBase;
        this.desireBase = desireBase;
        this.plans = plans;
        this.textLLMInference = textLLMInference;
        this.objectMapper = objectMapper;
    }
    
    public Pair<String, Boolean> doAction(final String userInput) {
    	
    	log.info("m=doAction, userInput={}", userInput);
    	
    	final TaskResponse nplTaskResponse = perceive(userInput);
    	
    	if(nplTaskResponse != null && nplTaskResponse.getFailbackUserMessage() != null && !nplTaskResponse.getFailbackUserMessage().trim().equals("")) {
    		return Pair.of(nplTaskResponse.getFailbackUserMessage(), false);
    	}else {
    		return Pair.of(deliberate(userInput, nplTaskResponse), true);
    	}	
    }

    @SneakyThrows
    private TaskResponse perceive(final String userInput) {
    	
    	log.info("m=perceive, userInput={}", userInput);
    	
    	final String systemPrompt = beliefBase
    			.getAllBeliefs()
    			.stream()
    			.map(Belief::handle)
    			.reduce((a,b)->"-".concat(a).concat("\n").concat(b))
    			.get();
    	
    	final String json = textLLMInference.complete(systemPrompt, userInput);
    	
    	try {
    		
    		return objectMapper.readValue(json, TaskResponse.class);
    	}catch (final Exception e) {
    		
    		log.error("m=perceive, result={}", json, e);
    		
    		return TaskResponse
    				.builder()
    				.failbackUserMessage(json)
    				.build();
		}
    }

    private String deliberate(final String userInput, final TaskResponse nplTaskResponse) {
    	
    	log.info("m=deliberate, taskResponse={}", nplTaskResponse);

    	final List<Desire> desires = desireBase.getApplicableDesires(beliefBase);
    	
    	final StringBuilder builder = new StringBuilder();
    	builder.append(nplTaskResponse + "nplTaskResponse:" + nplTaskResponse + "\n");
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
