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

    static final String MODERATION =
            """
                    verifique se a entrada do usuário condiz com o contexto e pode ser respondida.
                    responda apenas:
                    true -> caso sim, está dentro do contexto
                    false -> caso não, a solicitação trata outro assunto e diverge do contexto.
                    """;

    static final String RESUME =
            """
                    Extraia os principais tópicos e suas palavras chaves (PLN).
                    Por exemplo:
                    -tópico a
                        {entidades nomeadas}, {relacionamento das entidades}, {analise de sentimento}, {verbos}, {substantivos}, {objetos}, {datas}, {locais}, {pessoas}, {eventos}, {crenças}, {desejos}, {intenções}, {perguntas}, {respostas}
                    -tópico b
                        {entidades nomeadas}, {relacionamento das entidades}, {analise de sentimento}, {verbos}, {substantivos}, {objetos}, {datas}, {locais}, {pessoas}, {eventos}, {crenças}, {desejos}, {intenções}, {perguntas}, {respostas}
                    
                    Responda em formato de lista com os 3 tópicos mais relevantes e suas palavras chaves conforme exemplo.
                    """;

    private final FSM fsm;
    private final BDIRepo repo;
    private final LLMInference inference;

    public BDICtx(BDIRepo repo, FSM fsm, LLMInference inference) {
        this.repo = repo;
        this.fsm = fsm;
        this.inference = inference;
    }

    public BDICtx(BDIRepo repo, LLMInference inference) {
        this(repo, new BDIFsm(repo, inference), inference);
    }

    public String getContext() {
        return String.format("BeliefSummary: %s\nApplicableDesires:%s", this.beliefSummary(), this.desireSummary());
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

    Collection<Desire> getDesires() {
        Collection<Belief> beliefs = this.repo.getBeliefs();

        return this.repo.getDesires()
                .stream()
                .filter(desire -> beliefs
                        .stream()
                        .anyMatch(desire::isApplicable))
                .sorted(Comparator.comparingInt(Desire::getPriority))
                .toList();
    }

    Optional<State> next(final String input) {
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
