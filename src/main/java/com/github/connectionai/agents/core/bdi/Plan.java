package com.github.connectionai.agents.core.bdi;

import java.util.Arrays;
import java.util.List;

public interface Plan {
	
    String getDesireId();
    List<Action> getActions();
    
	static Plan defaultPlan() {
		return new Plan() {
			
			@Override
			public String getDesireId() {
				return "default-plan";
			}
			
			@Override
			public List<Action> getActions() {
				return Arrays.asList();
			}
		};
	}
}