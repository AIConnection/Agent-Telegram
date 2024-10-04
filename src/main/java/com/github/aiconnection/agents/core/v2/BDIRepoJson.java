package com.github.aiconnection.agents.core.v2;

import com.github.aiconnection.agents.core.bdi.AgentPlan;
import com.github.aiconnection.agents.core.bdi.BeliefBase;
import com.github.aiconnection.agents.core.bdi.DesireBase;
import com.github.aiconnection.agents.core.bdi.Plans;
import com.github.aiconnection.agents.core.bdi.pln.PLNBase;
import com.github.aiconnection.agents.core.fsm.FSM;
import org.metabot.core.bdi.core.Content;
import org.metabot.core.bdi.domain.*;
import org.metabot.core.bdi.domain.nlp.Task;
import org.metabot.core.bdi.repo.BDIRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("jsonBDI")
public class BDIRepoJson implements BDIRepo {

    private final PLNBase pln;
    private final BeliefBase beliefs;
    private final DesireBase desires;
    private final Plans plans;
    private final FSM fsm;

    @Autowired
    public BDIRepoJson(
            PLNBase pln,
            BeliefBase beliefs,
            DesireBase desires,
            Plans plans,
            FSM fsm
    ) {
        this.pln = pln;
        this.beliefs = beliefs;
        this.desires = desires;
        this.plans = plans;
        this.fsm = fsm;
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
                                    .map(act -> new Action<Content<String, String>, Content<String, String>>(act))
                                    .collect(Collectors.toList())
                                    .toArray(new Action[0])
                    ))
                    .toList();
        }
        return null;
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
        final String init = this.fsm.getInitialState().name();
        return this.fsm.getStates()
                .stream()
                .map(s -> new State(s.name(), s.name().equals(init),
                        s.transitions()
                                .stream()
                                .map(t -> new Transition(t.trigger(), t.target(), t.condition()))
                                .collect(Collectors.toList())
                                .toArray(new Transition[0])
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<State> getState(String id) {
        final String init = this.fsm.getInitialState().name();
        return this.fsm.get(id).map(s -> new State(s.name(), s.name().equals(init),
                s.transitions()
                        .stream()
                        .map(t -> new Transition(t.trigger(), t.target(), t.condition()))
                        .collect(Collectors.toList())
                        .toArray(new Transition[0])
        ));
    }

    @Override
    public boolean hasState(String id) {
        return this.fsm.getStates()
                .stream()
                .anyMatch(s -> s.name().equals(id));
    }
}
