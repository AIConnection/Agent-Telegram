package org.metabot.core.bdi.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.metabot.core.bdi.core.BDIParent;
import org.metabot.core.bdi.core.Content;
import org.springframework.util.Assert;

/**
 * Transition between states.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class Transition extends BDIParent {

    private final String target;
    private final String condition;

    public Transition(final String id, final String target) {
        this(id, null, target, null);
    }

    public Transition(final String id, final String target, final String condition) {
        this(id, null, target, condition);
    }

    public Transition(final String id, final String description, final String target, final String condition) {
        super(id, Type.TRANSITION, Content.of(description));
        Assert.notNull(target, "target must not be null");
        this.target = target;
        this.condition = condition;
    }

    private static final String HANDLE_TRIGGER = "-Trigger: '%s'. Target state: '%s'. ";

    private static final String HANDLE_CONDITION = "Condition to fulfill: '%s'. ";

    private static final String HANDLE_TEXT = "Please infer the next action based on this transition.";

    public boolean hasCondition() {
        return this.condition != null;
    }

    @Override
    public String toString() {
        return String.format(HANDLE_TRIGGER, this.getId(), target)
                + (this.hasCondition() ? String.format(HANDLE_CONDITION, condition) : "")
                + HANDLE_TEXT;
    }

}
