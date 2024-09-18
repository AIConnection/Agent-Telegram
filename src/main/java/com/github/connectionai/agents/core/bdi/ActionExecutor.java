package com.github.connectionai.agents.core.bdi;

import java.util.List;

public class ActionExecutor {

    public void executeActions(final List<Action> actions, final BeliefBase beliefBase) {
        for (Action action : actions) {
            action.execute(beliefBase);
        }
    }
}