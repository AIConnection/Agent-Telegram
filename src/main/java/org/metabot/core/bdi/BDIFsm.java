package org.metabot.core.bdi;

import org.metabot.core.bdi.core.LLMInference;
import org.metabot.core.bdi.domain.State;
import org.metabot.core.bdi.domain.Transition;
import org.metabot.core.bdi.fsm.FSM;
import org.metabot.core.bdi.repo.BDIRepo;

import java.util.Optional;
import java.util.stream.Collectors;

public class BDIFsm implements FSM {
    private static final String PROMPT_CHECK_CONDITION = "Dado o estado atual: '%s', " +
            "e a condição de transição: '%s', " +
            "a entrada do usuário: '%s' satisfaz a transição?";

    private static final String PROMPT_STATE_ANALYSIS = """
            Analisando a transição de estado:
            Entrada do usuário: '%s'
            Com base nisso, determine o próximo estado apropriado.""";

    private static final String PROMPT_TRANSITION_STATE = "Dado o estado atual: '%s', " +
            "a condição de transição: '%s', " +
            "e a entrada do usuário: '%s', qual deve ser o próximo estado?";

    private final BDIRepo repo;
    private final LLMInference inference;

    public BDIFsm(BDIRepo repo, LLMInference inference) {
        super();
        this.repo = repo;
        this.inference = inference;
    }

    @Override
    public State init() {
        return this.repo.getStates()
                .stream()
                .filter(State::isInitial)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("State initial not found"));
    }

    @Override
    public Optional<State> perceive(final String input) {

        final String system = this.prepareStateSystem();
        final String prompt = this.prepareStateAnalysis(input);

        final String currentState = this.inference.complete(system, prompt);

        if (this.repo.hasState(currentState)) {
            return this.repo.getState(currentState);
        } else {
            return Optional.of(this.init());
        }
    }

    @Override
    public boolean check(final Transition transition, final State currentState, final String userInput) {

        final String system = this.prepareStateSystem();
        final String prompt = this.prepareStateCheck(transition, currentState, userInput);
        final String conditionChecked = this.inference.complete(system, prompt);

        return Boolean.parseBoolean(conditionChecked);
    }

    @Override
    public Optional<State> next(final Transition transition, final State currentState, final String input) {

        final String system = this.prepareStateSystem();
        final String prompt = prepareStateNextTransition(transition, currentState, input);
        final String nextState = inference.complete(system, prompt);

        if (this.repo.hasState(nextState)) {
            return this.repo.getState(nextState);
        } else {

            return Optional.of(currentState);
        }
    }

    private String prepareStateSystem() {
        return this.repo.getStates()
                .stream()
                .map(State::toString)
                .collect(Collectors.joining("\n"));
    }

    private String prepareStateCheck(final Transition transition, final State currentState, final String userInput) {

        return String.format(
                PROMPT_CHECK_CONDITION,
                currentState.getId(),
                transition.getCondition(),
                userInput
        );
    }

    private String prepareStateAnalysis(final String userInput) {

        return String.format(
                PROMPT_STATE_ANALYSIS,
                userInput
        );
    }

    private String prepareStateNextTransition(final Transition transition, final State currentState, final String userInput) {

        return String.format(
                PROMPT_TRANSITION_STATE,
                currentState.getId(),
                transition.getCondition(),
                userInput
        );
    }
}
