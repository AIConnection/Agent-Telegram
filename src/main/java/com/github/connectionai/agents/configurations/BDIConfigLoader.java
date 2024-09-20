package com.github.connectionai.agents.configurations;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.connectionai.agents.core.bdi.Belief;
import com.github.connectionai.agents.core.bdi.BeliefBase;
import com.github.connectionai.agents.core.bdi.Desire;
import com.github.connectionai.agents.core.bdi.DesireBase;
import com.github.connectionai.agents.core.bdi.Desires;
import com.github.connectionai.agents.core.bdi.Plans;

import lombok.SneakyThrows;

@Configuration
public class BDIConfigLoader {

	@Autowired
    private final ObjectMapper mapper;
	
	@Autowired
	public BDIConfigLoader(final ObjectMapper mapper) {
		this.mapper = mapper;
	}

    @Bean
    public Plans plans() throws StreamReadException, DatabindException, IOException {

		return mapper.readValue(new File("bdi/plans.json"), Plans.class);
    }
    
    @SuppressWarnings("unchecked")
	@Bean
    @SneakyThrows
    public Map<String, Object> loadBeliefs() {
    	
    	return mapper.readValue(new File("bdi/beliefs.json"), Map.class);
    }
    
    @Bean
    @SneakyThrows
    public Desires loadDesires() {
    	
        return mapper.readValue(new File("bdi/desires.json"), Desires.class);
    }
    
    @Bean
    public BeliefBase beliefBase(@Autowired final List<Belief> loadBeliefs) {
    	
    	return new BeliefBase(loadBeliefs) {};
    }
    
    @Bean
    public DesireBase desireBase(@Autowired final List<Desire> loadDesires) {
    	
    	return new DesireBase(loadDesires) {};
    }
}