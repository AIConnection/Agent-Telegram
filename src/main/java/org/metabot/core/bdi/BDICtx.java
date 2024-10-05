package org.metabot.core.bdi;

import lombok.Getter;
import org.metabot.core.bdi.core.LLMInference;
import org.metabot.core.bdi.domain.Belief;
import org.metabot.core.bdi.domain.Desire;
import org.metabot.core.bdi.domain.State;
import org.metabot.core.bdi.domain.nlp.Task;
import org.metabot.core.bdi.fsm.FSM;
import org.metabot.core.bdi.repo.BDIRepo;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class BDICtx {

    private final FSM fsm;
    private final BDIRepo repo;
    private final LLMInference inference;

    public BDICtx(final BDIRepo repo, final FSM fsm, final LLMInference inference) {
        this.repo = repo;
        this.fsm = fsm;
        this.inference = inference;
    }

    public BDICtx(final BDIRepo repo, final LLMInference inference) {
        this(repo, new BDIFsm(repo, inference), inference);
    }

    public String getContext() {
        return String.format("BeliefSummary: %s%sApplicableDesires:%s", this.beliefSummary(), "\n", this.desireSummary());
    }

    public String beliefSummary() {
        return this.repo.getBeliefs()
                .stream()
                .map(Belief::toString)
                .collect(Collectors.joining("\n"));
    }

    public String desireSummary() {
        return this.getDesires()
                .stream()
                .map(Desire::toString)
                .collect(Collectors.joining("\n"));
    }

    public String stimulusSummary() {
        return this.repo.getTasks()
                .stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
    }

    protected Collection<Desire> getDesires() {
        final Collection<Belief> beliefs = this.repo.getBeliefs();

        return this.repo.getDesires()
                .stream()
                .filter(desire -> beliefs
                        .stream()
                        .anyMatch(desire::isApplicable))
                .sorted(Comparator.comparingInt(Desire::getPriority))
                .toList();
    }

    protected Optional<State> next(final String input) {
        final Optional<State> opt = fsm.perceive(input);
        final State state = opt.orElseGet(fsm::init);

        return state.getTransitions()
                .stream()
                .filter(transition -> fsm.check(transition, state, input))
                .map(transition -> fsm.next(transition, state, input))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

}
