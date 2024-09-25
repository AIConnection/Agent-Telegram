package com.github.aiconnection.agents.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.aiconnection.agents.adapters.secondary.inference.TextLLMInference;
import com.github.aiconnection.agents.core.bdi.BeliefBase;
import com.github.aiconnection.agents.core.bdi.Desire;
import com.github.aiconnection.agents.core.bdi.DesireBase;
import com.github.aiconnection.agents.core.bdi.Plans;
import com.github.aiconnection.agents.core.bdi.pln.PLNBase;
import com.github.aiconnection.agents.core.fsm.State;
import com.github.aiconnection.agents.core.fsm.StateTransitionHandler;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BDIService {

	private final StateTransitionHandler stateTransitionHandler;
    private final PLNBase plnBase;
    private final BeliefBase beliefBase;
    private final DesireBase desireBase;
    private final Plans plans;
    private final TextLLMInference textLLMInference;

    @Autowired
    public BDIService(
    		final StateTransitionHandler stateTransitionHandler,
            final PLNBase plnBase,
            final BeliefBase beliefBase, 
            final DesireBase desireBase, 
            final Plans plans,
            final TextLLMInference textLLMInference) {
    	
    	this.stateTransitionHandler = stateTransitionHandler;
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
    public String doAction(final String userInput) {
        log.info("m=doAction, userInput={}", userInput);

        final String perceptionResult = perceive(userInput);
        
        final String analysisResult = analyse(perceptionResult, userInput);

        return deliberate(userInput, analysisResult);
    }

    /**
     * Realiza a percepção da entrada do usuário, integrando com o sistema de PLN.
     * @param userInput entrada do usuário.
     * @return resultado da percepção.
     */
    @SneakyThrows
    private String perceive(final String userInput) {
        log.info("m=perceive, userInput={}", userInput);
        
        final State nextState = stateTransitionHandler.execute(userInput);

        final String systemPrompt = generateSystemPrompt(nextState);
        
        return inferUsingLLM(systemPrompt, userInput);
    }

    /**
     * Gera o prompt do sistema com base no PLN.
     * @return prompt gerado.
     */
    private String generateSystemPrompt(final State currentState) {
    	
    	return currentState.generateSystemPrompt(this.plnBase);
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
    private String analyse(final String perceptionResult, final String userInput) {
    	
        log.info("m=analyse, perceptionResult={}, userInput={}", perceptionResult, userInput);

        final String beliefSummary = generateBeliefSummary();
        
        return constructNarrative(beliefSummary, perceptionResult, userInput);
    }

    /**
     * Gera o sumário das crenças do agente.
     * @return sumário das crenças.
     */
    private String generateBeliefSummary() {
   
        try {
        	return beliefBase
            		.getAllBeliefs()
    				.stream()
    				.map(belief->">"
    						.concat(belief.getName())
    						.concat(":")
    						.concat(belief.getInitialValue())
    						.concat("\n"))
    				.reduce("", String::concat);
        }catch (final RuntimeException e) {
			log.error("m=generateBeliefSummary", e);
			
			throw e;
		}
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

        return textLLMInference.complete("faça uma contrução narrativa.", narrative);
    }
    
    
    public String buildNarrative(final String beliefSummary, final String taskResults, final String userInput) {
    	
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

        return combineResults(analysisResult, actions, userInput);
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
     * Combina os resultados da análise, ações e entrada do usuário.
     * @param analysisResult resultado da análise.
     * @param actions ações dos planos.
     * @param userInput entrada do usuário.
     * @return resultado final combinado.
     */
    private String combineResults(final String analysisResult, final String actions, final String userInput) {
    	
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
}
