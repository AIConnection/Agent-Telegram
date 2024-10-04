package org.metabot.core.bdi.domain;

import lombok.Getter;
import org.metabot.core.bdi.core.BDIParent;
import org.metabot.core.bdi.core.Content;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Possible states and transitions of BDI.
 */
@Getter
public class State extends BDIParent {

    private final boolean initial = false;
    private final List<Transition> transitions;

    public State(String id, Transition... transitions) {
        this(id, null, false, transitions);
    }

    public State(String id, boolean initial, Transition... transitions) {
        this(id, null, false, transitions);
    }

    public State(String id, String description, boolean initial, Transition... transitions) {
        super(id, Type.STATE, Content.of(description));
        this.transitions = Optional.ofNullable(transitions)
                .map(List::of)
                .orElse(List.of());
    }

    @Override
    public String toString() {
        return transitions
                .stream()
                .map(Transition::toString)
                .collect(Collectors.joining("\n"));
    }
}
