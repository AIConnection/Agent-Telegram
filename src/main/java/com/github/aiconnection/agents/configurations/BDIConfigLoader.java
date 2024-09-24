package com.github.aiconnection.agents.configurations;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.aiconnection.agents.core.bdi.Belief;
import com.github.aiconnection.agents.core.bdi.BeliefBase;
import com.github.aiconnection.agents.core.bdi.Beliefs;
import com.github.aiconnection.agents.core.bdi.Desire;
import com.github.aiconnection.agents.core.bdi.DesireBase;
import com.github.aiconnection.agents.core.bdi.Desires;
import com.github.aiconnection.agents.core.bdi.Plans;
import com.github.aiconnection.agents.core.bdi.pln.PLNBase;
import com.github.aiconnection.agents.core.bdi.pln.PLNTask;
import com.github.aiconnection.agents.core.bdi.pln.PLNTasks;

import lombok.SneakyThrows;

@Configuration
public class BDIConfigLoader {

    private final ObjectMapper mapper;
	
	@Autowired
	public BDIConfigLoader(final ObjectMapper mapper) {
		this.mapper = mapper;
	}
	
	@Bean
	public PLNTasks plnTasks() throws IOException {
	
		return mapper.readValue(new File("bdi/plnTasks.json"), PLNTasks.class);
	}

    @Bean
    public Plans plans() throws IOException {

		return mapper.readValue(new File("bdi/plans.json"), Plans.class);
    }
    
	@Bean
    @SneakyThrows
    public Beliefs loadBeliefs() {
    	
    	return mapper.readValue(new File("bdi/beliefs.json"), Beliefs.class);
    }
    
    @Bean
    @SneakyThrows
    public Desires loadDesires() {
    	
        return mapper
        		.readValue(new File("bdi/desires.json"), Desires.class);
    }
    
    @Bean
    public PLNBase plnBase(@Autowired final PLNTasks plnTasks) {
    	
    	return new PLNBase(plnTasks
    			.getItems()
    			.stream()
    			.map(PLNTask.class::cast)
    			.toList()) {}; 
    }
    
    @Bean
    public BeliefBase beliefBase(@Autowired final Beliefs beliefs) {
    	
    	return new BeliefBase(beliefs
    			.getItems()
    			.stream()
    			.map(Belief.class::cast)
    			.toList()) {};
    }
    
    @Bean
    public DesireBase desireBase(@Autowired final Desires desires) {
    	
    	return new DesireBase(desires
    			.getItems()
        		.stream()
        		.map(Desire.class::cast)
        		.toList()) {};
    }
}