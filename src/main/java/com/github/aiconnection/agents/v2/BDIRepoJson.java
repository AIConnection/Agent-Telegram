package com.github.aiconnection.agents.v2;

import com.github.aiconnection.agents.core.bdi.AgentPlan;
import com.github.aiconnection.agents.core.bdi.BeliefBase;
import com.github.aiconnection.agents.core.bdi.DesireBase;
import com.github.aiconnection.agents.core.bdi.Plans;
import com.github.aiconnection.agents.core.bdi.pln.PLNBase;
import com.github.aiconnection.agents.core.fsm.FSM;
import com.github.aiconnection.agents.core.service.HistoryService;
import org.jetbrains.annotations.NotNull;
import org.metabot.core.bdi.domain.*;
import org.metabot.core.bdi.domain.nlp.Task;
import org.metabot.core.bdi.repo.BDIRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component("jsonBDI")
public class BDIRepoJson implements BDIRepo {

    private final PLNBase pln;
    private final BeliefBase beliefs;
    private final DesireBase desires;
    private final Plans plans;
    private final FSM fsm;
    private final HistoryService history;

    @Autowired
    public BDIRepoJson(
            PLNBase pln,
            BeliefBase beliefs,
            DesireBase desires,
            Plans plans,
            FSM fsm,
            HistoryService history
    ) {
        this.pln = pln;
        this.beliefs = beliefs;
        this.desires = desires;
        this.plans = plans;
        this.fsm = fsm;
        this.history = history;
    }

    @Override
    public Collection<Belief> getBeliefs() {
        return this.beliefs.getAllBeliefs()
                .stream()
                .map(b -> new Belief(b.getName(), b.getInitialValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Desire> getDesires() {
        return this.desires.getDesires()
                .stream()
                .map(d -> new Desire(d.getDesireId(), d.getPriority(), d.getConditions().toArray(new String[0])))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Intention> getIntentions() {
        final Map<String, List<AgentPlan>> desirePlans = this.plans.getItems()
                .stream()
                .collect(Collectors.groupingBy(AgentPlan::getDesireId));

        final List<Intention> intentions = new ArrayList<>();

        for (Map.Entry<String, List<AgentPlan>> entry : desirePlans.entrySet()) {
            Optional<Desire> desire = this.getDesires()
                    .stream()
                    .filter(d -> d.getId().equals(entry.getKey()))
                    .findFirst();

            List<Plan> plans = entry.getValue()
                    .stream()
                    .map(p -> new Plan(p.getId(),
                            p.getActions()
                                    .stream()
                                    .map(Action::new)
                                    .toList()
                                    .toArray(new Action[0])
                    ))
                    .toList();

            Intention intention = desire
                    .map(d -> new Intention(UUID.randomUUID().toString(), d, plans.toArray(new Plan[0])))
                    .orElse(new Intention(UUID.randomUUID().toString(), new Desire(entry.getKey()), plans.toArray(new Plan[0])));
            intentions.add(intention);
        }

        return intentions;
    }

    @Override
    public Collection<Task> getTasks() {
        return this.pln.getItems()
                .stream()
                .map(p -> new Task(p.getName(), p.getDescription()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<State> getStates() {
        final State init = Optional.of(this.fsm.getInitialState())
                .map(s -> getState(s, true))
                .get();

        final List<State> states = new LinkedList<>(List.of(init));

        states.addAll(this.fsm.getStates()
                .stream()
                .map(s -> getState(s, false))
                .toList());

        return states;
    }

    @NotNull
    private static State getState(com.github.aiconnection.agents.core.fsm.State s, boolean initial) {
        return new State(s.name(), initial,
                s.transitions()
                        .stream()
                        .map(t -> new Transition(t.trigger(), t.target(), t.condition()))
                        .toList()
                        .toArray(new Transition[0])
        );
    }

    @Override
    public Optional<State> getState(String id) {
        final Optional<State> init = Optional.of(this.fsm.getInitialState())
                .filter(s -> id.contains(s.name()))
                .map(s -> getState(s, true));

        if (init.isPresent()) {
            return init;
        }

        return this.fsm.getStates()
                .stream()
                .filter(s -> id.contains(s.name()))
                .findFirst()
                .map(s -> getState(s, false));
    }

    @Override
    public boolean hasState(String content) {
        return this.fsm.getStates()
                .stream()
                .anyMatch(s -> content.contains(s.name()));
    }

    @Override
    public Optional<String> getHistory(String id) {
        return history.getCurrent(Long.valueOf(id));
    }

    @Override
    public void addHistory(String id, String content, Object... opts) {
        history.add(Long.valueOf(id), content, opts);
    }

    @Override
    public void cleanHistory(String id) {
        history.clean(Long.valueOf(id));
    }
}
