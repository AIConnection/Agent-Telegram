package com.github.aiconnection.agents.core.bdi;

import java.util.Arrays;
import java.util.List;

public interface Plan {
	
	public static Plan defaultPlan() {
		return new Plan() {
			
			@Override
			public String getId() {
				return "plan-id";
			}
			
			@Override
			public String getDesireId() {
				return "desire-id";
			}
			
			@Override
			public List<String> getActions() {
				return Arrays.asList();
			}
		};
	}
	
	String getId();
    String getDesireId();
    List<String> getActions();    
}