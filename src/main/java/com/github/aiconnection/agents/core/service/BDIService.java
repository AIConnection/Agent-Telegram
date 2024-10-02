package com.github.aiconnection.agents.core.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.aiconnection.agents.adapters.secondary.inference.TextLLMInference;
import com.github.aiconnection.agents.core.bdi.BeliefBase;
import com.github.aiconnection.agents.core.bdi.Desire;
import com.github.aiconnection.agents.core.bdi.DesireBase;
import com.github.aiconnection.agents.core.bdi.Plans;
import com.github.aiconnection.agents.core.bdi.pln.PLNBase;
import com.github.aiconnection.agents.core.fsm.State;
import com.github.aiconnection.agents.core.fsm.TransitionHandler;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BDIService {

	private final TransitionHandler transitionHandler;
    private final PLNBase plnBase;
    private final BeliefBase beliefBase;
    private final DesireBase desireBase;
    private final Plans plans;
    private final TextLLMInference textLLMInference;

    @Autowired
    public BDIService(
    		final TransitionHandler transitionHandler,
            final PLNBase plnBase,
            final BeliefBase beliefBase, 
            final DesireBase desireBase, 
            final Plans plans,
            final TextLLMInference textLLMInference) {
    	
    	this.transitionHandler = transitionHandler;
        this.plnBase = plnBase;
        this.beliefBase = beliefBase;
        this.desireBase = desireBase;
        this.plans = plans;
        this.textLLMInference = textLLMInference;
    }

    /**
     * Método principal que organiza o fluxo de percepção, análise e deliberação do agente BDI.
     * @param userInput entrada do usuário.
     * @return Par com a resposta do agente e um status booleano.
     */
    public String processUserInput(final String userInput) {
    	
        log.info("m=processUserInput, userInput={}", userInput);

        final String preprocessedStimulus = preprocessStimulus();
        
        final String inferredStimulus = inferPreprocessedStimulus(preprocessedStimulus, userInput);
        
        final String perceptionResult = perceive(inferredStimulus, userInput);
        
        final String analysisResult = analyze(perceptionResult, userInput);
        
        final String delibarate = deliberate(userInput, analysisResult);

        return responseFormulate(userInput, delibarate);
    }

	private String preprocessStimulus() {

        return plnBase
                .handles()
                .stream()
                .collect(Collectors.joining());
    }

    private String inferPreprocessedStimulus(final String preprocessedStimulus, final String userInput) {
    	
        return inferUsingLLM(preprocessedStimulus, userInput);
    }

	/**
     * Realiza a percepção da entrada do usuário, integrando com o sistema de PLN.
     * @param userInput entrada do usuário.
     * @return resultado da percepção.
     */
    @SneakyThrows
    private String perceive(final String preprocessedStimulus, final String userInput) {
        
    	log.info("m=perceive, preprocessedStimulus={}, userInput={}", preprocessedStimulus, userInput);
        
        final State nextState = transitionHandler.execute(userInput);

        final String fsmPrompt = generateSystemPrompt(nextState);
        
        return inferUsingLLM(String.format("até o momento o que foi percebido pelo agente:\npreprocessedStimulus: %s\nfsmPrompt: %s\nnextState: %s", preprocessedStimulus, fsmPrompt, nextState), userInput);
    }

    /**
     * Gera o prompt do sistema com base no PLN.
     * @return prompt gerado.
     */
    private String generateSystemPrompt(final State currentState) {
    	
    	return currentState.handle();
    }

    /**
     * Realiza a inferência com base no LLM.
     * @param systemPrompt prompt do sistema.
     * @param userInput entrada do usuário.
     * @return resultado da inferência.
     */
    private String inferUsingLLM(final String systemPrompt, final String userInput) {
    	
        return textLLMInference.complete(systemPrompt, userInput);
    }

    /**
     * Analisa a percepção e o contexto de crenças do agente.
     * @param perceptionResult resultado da percepção.
     * @param userInput entrada do usuário.
     * @return resultado da análise.
     */
    private String analyze(final String perceptionResult, final String userInput) {
    	
        log.info("m=analyze, perceptionResult={}, userInput={}", perceptionResult, userInput);

        final String beliefSummary = generateBeliefSummary();
        
        return constructNarrative(beliefSummary, perceptionResult, userInput);
    }

    /**
     * Gera o sumário das crenças do agente.
     * @return sumário das crenças.
     */
    private String generateBeliefSummary() {
   
    	return beliefBase
        		.getAllBeliefs()
				.stream()
				.map(belief->">"
						.concat(belief.getName())
						.concat(":")
						.concat(belief.getInitialValue())
						.concat("\n"))
				.reduce("", String::concat);
    }

    /**
     * Constrói uma narrativa baseada no sumário de crenças, percepção e entrada do usuário.
     * @param beliefSummary sumário das crenças.
     * @param perceptionResult resultado da percepção.
     * @param userInput entrada do usuário.
     * @return narrativa construída de forma significativa.
     */
    private String constructNarrative(final String beliefSummary, final String perceptionResult, final String userInput) {
        
    	log.info("m=constructNarrative, userInput={}", userInput);

        final String narrative = buildNarrative(beliefSummary, perceptionResult, userInput);

        return textLLMInference.complete("faça uma construção narrativa.", narrative);
    }

    /**
     * Monta a estrutura básica da narrativa.
     * @param beliefSummary sumário das crenças.
     * @param taskResults resultados das tarefas de PLN.
     * @param userInput entrada do usuário.
     * @return estrutura básica da narrativa.
     */
    private String buildNarrative(final String beliefSummary, final String taskResults, final String userInput) {
        
    	return new StringBuilder()
                .append("Resumo das Crenças: ")
                .append(beliefSummary)
                .append("\n")
                .append("Resultados das tarefas de PLN:\n")
                .append(taskResults)
                .append("Entrada do usuário: ")
                .append(userInput)
                .append("\n")
                .append("Com base nisso, o agente está avaliando as próximas ações.\n")
                .toString();
    }

    /**
     * Realiza a deliberação com base nas crenças, desejos e planos do agente.
     * @param userInput entrada do usuário.
     * @param analysisResult resultado da análise.
     * @return resultado da deliberação.
     */
    private String deliberate(final String userInput, final String analysisResult) {
    	
        log.info("m=deliberate, analysisResult={}", analysisResult);

        final List<Desire> applicableDesires = getApplicableDesires();
        final String actions = generatePlanActions(applicableDesires);

        return compileDeliberationResult(analysisResult, actions, userInput);
    }
    
    private String responseFormulate(final String userInput, final String delibarate) {
    	
		return textLLMInference.complete(String.format("um agente deliberou [delibarate] sobre os dados de entrada do usuário [userInput], formate de forma adequada para responder ao usuário, responda apenas o que deve ser enviado para o usuário. %suserInput:%s%sdelibarate:%s", "\n", userInput, "\n", delibarate));
	}

    /**
     * Obtém os desejos aplicáveis com base nas crenças do agente.
     * @return lista de desejos aplicáveis.
     */
    private List<Desire> getApplicableDesires() {
    	
        return desireBase.getApplicableDesires(beliefBase);
    }

    /**
     * Gera as ações dos planos associados aos desejos aplicáveis.
     * @param desires lista de desejos aplicáveis.
     * @return ações geradas.
     */
    private String generatePlanActions(final List<Desire> desires) {
    	
        final StringBuilder builder = new StringBuilder();
        
        plans
        	.getItems()
        	.stream()
            .filter(plan -> desires.contains(findDesire(desires, plan.getDesireId())))
            .forEach(plan -> plan
            		.getActions()
            		.forEach(action -> builder
            				.append("-")
            				.append(action)
            				.append("\n")));
        
        return builder.toString();
    }

    /**
     * Compila os resultados da análise, ações e entrada do usuário.
     * @param analysisResult resultado da análise.
     * @param actions ações dos planos.
     * @param userInput entrada do usuário.
     * @return resultado final compilado.
     */
    private String compileDeliberationResult(final String analysisResult, final String actions, final String userInput) {
    	
        return textLLMInference.complete(String.join("\n", 
            "Analysis: " + analysisResult, 
            "Actions: " + actions, 
            "User Input: " + userInput));
    }

    /**
     * Encontra um desejo específico na lista de desejos.
     * @param desires lista de desejos.
     * @param desireId ID do desejo a ser encontrado.
     * @return desejo correspondente ao ID.
     */
    private Desire findDesire(final List<Desire> desires, final String desireId) {
    	
        return desires
        		.stream()
        		.filter(desire -> desire.getDesireId().equalsIgnoreCase(desireId))
        		.findFirst()
        		.orElseThrow(() -> new RuntimeException("Desire not found for ID: " + desireId));
    }

	public String getContextDescription() {
		
		return String.format("BeliefSummary: %s%sApplicableDesires:%s", generateBeliefSummary(), "\n", getApplicableDesires());
	}
}
