package org.metabot.core.bdi.domain;

import lombok.Getter;
import org.metabot.core.bdi.core.BDIParent;
import org.metabot.core.bdi.core.Content;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class Desire extends BDIParent {
    private final int priority;
    private final List<String> conditions;

    public Desire(String id, String... conditions) {
        this(id, 0, conditions);
    }

    public Desire(String id, int priority, String... conditions) {
        this(id, null, priority, conditions);
    }

    public Desire(String id, String description, int priority, String... conditions) {
        super(id, Type.DESIRE, Content.of(description));
        this.priority = priority;
        this.conditions = Optional.ofNullable(conditions)
                .map(List::of)
                .orElseGet(List::of);
    }

    public Boolean isApplicable(final Belief belief) {
        return conditions.stream()
                .map(condition -> eval(condition, belief))
                .reduce((a, b) -> a && b)
                .isPresent();
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + "PRIORITY: " + priority +
                conditions
                        .stream()
                        .map(cond -> "-> CONDITION: " + cond)
                        .collect(Collectors.joining("\n"));
    }

    private Boolean eval(final String condition, final Belief belief) {

        final String[] expressions = condition.split("[ ]");
        return process(expressions, belief);
    }

    private Boolean process(final String[] expression, final Belief belief) {

        return Arrays.asList(expression)
                .stream()
                .filter(part -> belief.getId().trim().equalsIgnoreCase(part.trim()))
                .findFirst()
                .isPresent();
    }
}
