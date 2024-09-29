package com.github.aiconnection.agents.core.service;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service("intelligentAgentService")
@Slf4j
public class IntelligentAgentService implements AgentService{
	
	private static final String MODERATION = 
			"""
			verifique se a entrada do usuário condiz com o contexto e pode ser respondida.
			responda apenas:
			true -> caso sim, está dentro do contexto
			false -> caso não, a solicitação trata outro assunto e diverge do contexto.
			""";
	
	private static final String RESUME =
			"""
			Extraia os principais tópicos e suas paravras chaves (PLN).
			Por exemplo:
			-tópico a
			    {entidades nomeadas}, {relacionamento das entedades}, {analise de sentimento}, {verbos}, {substantivos}, {objetos}, {datas}, {locais}, {pessoas}, {eventos}, {crenças}, {desejos}, {intenções}, {perguntas}, {respostas}
			-tópico b
			    {entidades nomeadas}, {relacionamento das entedades}, {analise de sentimento}, {verbos}, {substantivos}, {objetos}, {datas}, {locais}, {pessoas}, {eventos}, {crenças}, {desejos}, {intenções}, {perguntas}, {respostas}
			    
			Responda em formato de lista com os 3 tópicos mais relevantes e suas paravras chaves conforme exemplo.
			""";

	private final LLMInference llmInference;
	
	private final RecognitionService recognition;
	
	private final ConversionService conversion;
	
	private final BDIService bdiService;
	
	private final String botToken;

	public IntelligentAgentService(
			@Value("${botToken}") final String botToken,
			@Autowired final LLMInference llmInference,
			@Autowired final RecognitionService recognition,
			@Autowired final ConversionService conversion,
			@Autowired final BDIService bdiService) {
		
		this.botToken = botToken;
		this.llmInference = llmInference;
		this.recognition = recognition;
		this.conversion = conversion;
		this.bdiService = bdiService;
	}
	
	@Override
	public String getBotToken() {
		
		return botToken;
	}

	@Override
	public byte[] convert(final URL sourceFile) {
		
		log.info("m=convert, sourceFile={}", sourceFile);
		
		return conversion.convert(sourceFile);
	}

	@Override
	public String convertVoiceToText(final byte[] voiceData) {
		
		log.info("m=convertVoiceToText, voiceData.length={}", voiceData.length);
		
		return recognition.convertVoiceToText(voiceData);
	}

	@Override
	public String complete(final String userPrompt) {
		
		log.info("m=complete, userPrompt={}", userPrompt);
		
		return bdiService.processUserInput(userPrompt);
	}

	
	@Override
	public String complete(final String systemPrompt, final String userPrompt, final List<String> prompts) {
		
		log.info("m=complete, systemPrompt={}, userPrompt={}, prompts={}", systemPrompt, userPrompt, Arrays.toString(prompts.toArray()));
		
		final StringBuilder builder = new StringBuilder();
		
		prompts.forEach(prompt -> builder.append(bdiService.processUserInput(prompt)));
		
		return llmInference.complete(systemPrompt, userPrompt, prompts);
	}

	@Override
	public String complete(final String systemPrompt, final String prompt) {
		
		log.info("m=complete, systemPrompt={}, prompt={}", systemPrompt, prompt);
		
		final String result = bdiService.processUserInput(prompt);
		
		return llmInference.complete(systemPrompt, result);
	}

	@Override
	public String resume(final String text) {
		
		return llmInference.complete(RESUME, text);
	}

	@Override
	public boolean moderation(final String userInput) {
		
		return BooleanUtils.toBoolean(llmInference.complete(MODERATION, String.format("Context:%s%suserInput:%s", bdiService.getContextDescription(),"\n", userInput)));
	}

	@Override
	public String getModeratedContent() {
		return " Parece que sua mensagem não está relacionada com o tipo de serviço que eu ofereço.";
	}
}
