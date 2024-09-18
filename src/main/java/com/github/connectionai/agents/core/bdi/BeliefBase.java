package com.github.connectionai.agents.core.bdi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class BeliefBase {

    private final Map<String, Object> beliefs;

    public BeliefBase(final Map<String, Object> initialBeliefs) {
    	
        this.beliefs = Collections.synchronizedMap(initialBeliefs);
    }

    public Object getBelief(final String name) {
    	
        return beliefs.get(name);
    }

    public void updateBelief(final String name, final Object value) {
    	
        beliefs.put(name, value);
    }

    public Map<String, Object> getAllBeliefs() {
    	
        return beliefs;
    }

	@SuppressWarnings("unchecked")
	public void addHistory(final String topic, final Object response) {
		
		if(beliefs.containsKey(topic)) {
			
			((List<Object>)beliefs.get(topic)).add(response);
			
		}else {
			
			beliefs.put(topic, Collections.synchronizedList(Arrays.asList(response)));
		}
	}
}