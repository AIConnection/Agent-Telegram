package com.github.connectionai.agents.core.bdi;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ActionExecutor {

    public void executeActions(final List<Action> actions, final String prompt) {
        
    	actions.forEach(action->action.execute(prompt));
    }
}