package com.github.connectionai.agents.core.bdi;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BeliefBase {

    private final List<Belief> beliefs;

    public BeliefBase(final List<Belief> beliefs) {
    	
        this.beliefs = beliefs;
    }

    public Belief getBelief(final String name) {
    	
        return beliefs
        		.stream()
        		.filter(belief->belief.getName().equalsIgnoreCase(name))
        		.findFirst()
        		.orElse(null);
    }

    public void updateBelief(final Belief belief) {
    	
    	final int indexOf = beliefs.indexOf(belief);
    	if(indexOf > 0) {
    		beliefs.set(indexOf, belief);
    	}
    }

    public Collection<Belief> getAllBeliefs() {
    	
        return beliefs;
    }

	public void addHistory(final Belief belief) {
		
		beliefs.add(belief);
	}
	
	public List<String> handles(){
		
		return beliefs
				.stream()
				.map(belief->belief.handle())
				.collect(Collectors.toList());
	}
}