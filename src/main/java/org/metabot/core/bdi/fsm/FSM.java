package org.metabot.core.bdi.fsm;

import org.metabot.core.bdi.domain.State;
import org.metabot.core.bdi.domain.Transition;

import java.util.Optional;

/**
 * Interface represents finite state machine with basic methods.
 */
public interface FSM {

    State init();

    Optional<State> next(final Transition transition, final State current, final String input);

    Optional<State> perceive(final String input);

    boolean check(final Transition transition, final State current, final String input);

}
