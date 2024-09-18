package com.github.connectionai.agents.core.actions;
import org.springframework.stereotype.Component;

import com.github.connectionai.agents.core.bdi.Action;
import com.github.connectionai.agents.core.bdi.BeliefBase;

@Component
public class ProvideResponseAction implements Action {

    @Override
    public void execute(final BeliefBase beliefBase) {

        final Object response = beliefBase.getBelief("response");

        final Object history = beliefBase.getBelief("conversationHistory");
        
        final String topic = extractTopic(response);
        
        beliefBase.addHistory(topic, response);
        
        beliefBase.updateBelief("conversationHistory", history);
    }

	private String extractTopic(final Object response) {
		return String.valueOf(response);
	}
}