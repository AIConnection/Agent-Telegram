package org.metabot.core.bdi.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.metabot.core.bdi.core.BDIParent;
import org.metabot.core.bdi.core.Content;

import java.util.List;
import java.util.Optional;

@Getter
@EqualsAndHashCode(callSuper = true)
public class Plan extends BDIParent {

    private final List<Action> actions;

    public Plan(final String id, final Action... actions) {
        this(id, null, actions);
    }

    public Plan(final String id, final String description, final Action... actions) {
        super(id, Type.PLAN, Content.of(description));
        this.actions = Optional.ofNullable(actions)
                .map(List::of)
                .orElseGet(List::of);
    }

}
