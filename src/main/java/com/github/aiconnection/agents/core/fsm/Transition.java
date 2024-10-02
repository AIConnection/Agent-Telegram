package com.github.aiconnection.agents.core.fsm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Builder
public record Transition(String trigger, String target, String condition) {

    private static final String HANDLE_TRIGGER = "-Trigger: '%s'. Target state: '%s'. Condition to fulfill: '%s'. Please infer the next action based on this transition.\n";

    @JsonCreator
    public Transition(
            @JsonProperty("trigger") final String trigger,
            @JsonProperty("target") final String target,
            @JsonProperty("condition") final String condition) {

        this.trigger = trigger;
        this.target = target;
        this.condition = condition;
    }

    @JsonIgnore
    public String handle() {

        return String.format(
                HANDLE_TRIGGER,
                trigger,
                target,
                condition
        );
    }
}