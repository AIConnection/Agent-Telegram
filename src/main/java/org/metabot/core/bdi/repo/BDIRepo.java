package org.metabot.core.bdi.repo;

import org.metabot.core.bdi.domain.Belief;
import org.metabot.core.bdi.domain.Desire;
import org.metabot.core.bdi.domain.Intention;
import org.metabot.core.bdi.domain.State;
import org.metabot.core.bdi.domain.nlp.Task;

import java.util.Collection;
import java.util.Optional;

public interface BDIRepo {

    Collection<Belief> getBeliefs();

    Collection<Desire> getDesires();

    Collection<Intention> getIntentions();

    Collection<Task> getTasks();

    Collection<State> getStates();

    Optional<State> getState(final String id);

    boolean hasState(final String id);

    void addHistory(final String id, final String history, final Object... opts);

    Optional<String> getHistory(final String id);

    void cleanHistory(final String id);
}
