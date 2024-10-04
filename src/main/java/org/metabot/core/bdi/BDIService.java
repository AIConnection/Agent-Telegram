package org.metabot.core.bdi;

import org.metabot.core.bdi.core.NLPService;
import org.metabot.core.bdi.domain.Action;
import org.metabot.core.bdi.domain.Desire;
import org.metabot.core.bdi.domain.State;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.metabot.core.bdi.BDICtx.MODERATION;
import static org.metabot.core.bdi.BDICtx.RESUME;

public record BDIService(BDICtx ctx) implements NLPService {

    /**
     * Process user input into flow stimulus, perception, analyze and deliberate of the BDI.
     *
     * @param userInput User input text.
     * @return Pair of BDI response and boolean status.
     */
    @Override
    public String process(final String userInput) {
        final String stimulus = this.stimulus(userInput);
        final String perception = this.perceive(userInput, stimulus);
        final String analysis = this.analyze(userInput, perception);
        final String deliberate = this.deliberate(userInput, analysis);

        return this.response(userInput, deliberate);
    }

    /**
     * Resumes the input text by inference.
     *
     * @param text Input text.
     * @return Resume to text.
     */
    @Override
    public String resume(final String text) {
        return this.ctx.getInference().complete(RESUME, text);
    }

    /**
     * Moderates the user input.
     *
     * @param userInput User input text.
     * @return <code>true</code>: moderate else not moderate.
     */
    @Override
    public boolean moderation(final String userInput) {
        final String input = String.format("Context:%s\nuserInput:%s", this.ctx.getContext(), userInput);
        return Boolean.parseBoolean(this.ctx.getInference().complete(MODERATION, input));
    }

    public String getModerateContent() {
        return " Parece que sua mensagem não está relacionada com o tipo de serviço que eu ofereço.";
    }

    private String stimulus(String userInput) {
        return this.ctx.getInference().complete(this.ctx.stimulusSummary(), userInput);
    }

    private String perceive(final String userInput, final String stimulus) {
        final State nextState = this.ctx.next(userInput).orElseThrow(() -> new IllegalStateException("Next state not found"));

        return this.ctx.getInference()
                .complete(String.format("até o momento o que foi percebido pelo agente:\npreprocessedStimulus: %s\nfsmPrompt: %s\nnextState: %s",
                        stimulus, nextState.toString(), nextState.getId()), userInput);
    }

    private String analyze(final String userInput, final String perception) {
        final String narrative = buildNarrative(perception, userInput);

        return this.ctx.getInference().complete("faça uma construção narrativa.", narrative);
    }

    private String deliberate(final String userInput, final String analysis) {
        final Collection<Desire> desires = this.ctx.getDesires();
        final String actions = this.ctx.getRepo().getIntentions()
                .stream()
                .filter(intention -> desires.contains(intention.getDesire()))
                .flatMap(intention -> intention.getPlans().stream())
                .flatMap(plan -> plan.getActions().stream())
                .map(Action::toString)
                .collect(Collectors.joining("\n"));

        return compileDeliberationResult(analysis, actions, userInput);
    }

    private String response(final String userInput, final String deliberate) {
        return this.ctx.getInference().complete(
                String.format("um agente deliberou [deliberate] sobre os dados de entrada do usuário [userInput], formate de forma adequada para responder ao usuário, responda apenas o que deve ser enviado para o usuário. %suserInput:%s%sdeliberate:%s",
                        "\n", userInput, "\n", deliberate));
    }

    private String compileDeliberationResult(final String analysis, final String actions, final String userInput) {

        return this.ctx.getInference().complete(String.join("\n",
                "Analysis: " + analysis,
                "Actions: " + actions,
                "User Input: " + userInput));
    }

    private String buildNarrative(final String taskResults, final String userInput) {

        return "Resumo das Crenças: " + this.ctx.beliefSummary()
                + "\n" +
                "Resultados das tarefas de PLN: " + taskResults
                + "\n" +
                "Entrada do usuário: " + userInput +
                "\nCom base nisso, o agente está avaliando as próximas ações.\n";
    }
}
