package com.github.aiconnection.agents.core.bdi;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AgentDesire implements Desire {

    private final String desireId;
    private final Integer priority;
    private final List<String> conditions;

    @JsonCreator
    public AgentDesire(
            @JsonProperty("desireId") final String desireId,
            @JsonProperty("priority") final Integer priority,
            @JsonProperty("conditions") final List<String> conditions) {

        this.desireId = desireId;
        this.priority = priority;
        this.conditions = conditions;
    }

    @Override
    public Boolean isApplicable(final BeliefBase beliefBase) {
        final Collection<Belief> beliefs = beliefBase.getAllBeliefs();

        return conditions.stream()
                .map(condition -> eval(condition, beliefs))
                .reduce((a, b) -> a && b)
                .isPresent();
    }

    private Boolean eval(final String condition, final Collection<Belief> beliefs) {

        final String[] expression = condition.split("[ ]");

        return beliefs
                .stream()
                .map(belief -> process(expression, belief))
                .reduce((a, b) -> a && b)
                .orElse(Boolean.FALSE);
    }

    private Boolean process(final String[] expression, final Belief belief) {

        return Arrays.asList(expression)
                .stream()
                .filter(part -> belief.getName().trim().equalsIgnoreCase(part.trim()))
                .findFirst()
                .isPresent();
    }
}
