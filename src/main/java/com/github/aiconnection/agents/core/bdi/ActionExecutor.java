package com.github.aiconnection.agents.core.bdi;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ActionExecutor {
	
    public String executeActions(final List<Action> actions, final String prompt) {
    	
    	log.info("m=executeActions, actions={}, prompt={}", Arrays.toString(actions.toArray()), prompt);
    	
        final StringBuilder builder = new StringBuilder();
    	
        actions.forEach(action->builder.append(prompt + ":" + action.execute(prompt).concat("\n")));
    	
        return builder.toString();
    }
}