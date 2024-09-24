package com.github.aiconnection.agents.core.bdi;

import java.util.Collection;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BeliefBase {

    private final List<Belief> items;

    public BeliefBase(final List<Belief> items) {
    	
        this.items = items;
    }

    public Belief getBelief(final String name) {
    	
    	log.info("m=getBelief, name={}", name);
    	
        return items
        		.stream()
        		.filter(belief->belief.getName().equalsIgnoreCase(name))
        		.findFirst()
        		.orElse(null);
    }

    public void updateBelief(final Belief belief) {
    	
    	log.info("m=updateBelief, belief={}", belief);
    	
    	final int indexOf = items.indexOf(belief);
    	if(indexOf > 0) {
    		items.set(indexOf, belief);
    	}
    }

    public Collection<Belief> getAllBeliefs() {
    	
    	log.info("m=getAllBeliefs");
    	
        return items;
    }

	public void addHistory(final Belief belief) {
		
		log.info("m=addHistory, belief={}", belief);
		
		items.add(belief);
	}
}