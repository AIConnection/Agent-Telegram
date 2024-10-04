package org.metabot.core.bdi.domain;

import lombok.Getter;
import org.metabot.core.bdi.core.BDIParent;
import org.metabot.core.bdi.core.Content;

import java.util.List;
import java.util.Optional;

@Getter
public class Plan extends BDIParent {

    private final List<Action> actions;

    public Plan(String id, Action... actions) {
        this(id, null, actions);
    }

    public Plan(String id, String description, Action... actions) {
        super(id, Type.PLAN, Content.of(description));
        this.actions = Optional.ofNullable(actions)
                .map(List::of)
                .orElse(List.of());
    }

}
