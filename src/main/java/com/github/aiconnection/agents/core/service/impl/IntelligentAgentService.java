package com.github.aiconnection.agents.core.service.impl;

import com.github.aiconnection.agents.core.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.metabot.core.bdi.core.Agent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("intelligentAgentService")
public class IntelligentAgentService implements AgentService, Agent {

    private static final String MODERATION =
            """
                    verifique se a entrada do usuário condiz com o contexto e pode ser respondida.
                    responda apenas:
                    true -> caso sim, está dentro do contexto
                    false -> caso não, a solicitação trata outro assunto e diverge do contexto.
                    """;

    private static final String RESUME =
            """
                    Extraia os principais tópicos e suas palavras chaves (PLN).
                    Por exemplo:
                    -tópico a
                        {entidades nomeadas}, {relacionamento das entidades}, {analise de sentimento}, {verbos}, {substantivos}, {objetos}, {datas}, {locais}, {pessoas}, {eventos}, {crenças}, {desejos}, {intenções}, {perguntas}, {respostas}
                    -tópico b
                        {entidades nomeadas}, {relacionamento das entidades}, {analise de sentimento}, {verbos}, {substantivos}, {objetos}, {datas}, {locais}, {pessoas}, {eventos}, {crenças}, {desejos}, {intenções}, {perguntas}, {respostas}
                    
                    Responda em formato de lista com os 3 tópicos mais relevantes e suas palavras chaves conforme exemplo.
                    """;

    private final LLMInference llmInference;

    private final RecognitionService recognition;

    private final ConversionService conversion;

    private final BDIService bdiService;

    private final String botToken;
    private final HistoryService historyService;

    @Autowired
    public IntelligentAgentService(
            @Value("${botToken}") final String botToken,
            final BDIService bdiService,
            final HistoryService historyService,
            @Qualifier("voiceRecognition") final RecognitionService recognition,
            @Qualifier("audioConversion") final ConversionService conversion,
            @Qualifier("textLLMInference") final LLMInference llmInference
    ) {

        this.botToken = botToken;
        this.bdiService = bdiService;
        this.historyService = historyService;
        this.recognition = recognition;
        this.conversion = conversion;
        this.llmInference = llmInference;
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

        final StringBuilder builder = new StringBuilder(userPrompt);

        prompts.forEach(prompt -> builder.append(bdiService.processUserInput(prompt)));

        return llmInference.complete(systemPrompt, builder.toString());
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

        return BooleanUtils.toBoolean(llmInference.complete(MODERATION, String.format("Context:%s%suserInput:%s", bdiService.getContextDescription(), "\n", userInput)));
    }

    @Override
    public String getModeratedContent() {
        return " Parece que sua mensagem não está relacionada com o tipo de serviço que eu ofereço.";
    }

    @Override
    public String process(URL url) {
        return process(url, null);
    }

    @Override
    public String process(byte[] bytes) {
        return process(bytes, null);
    }

    @Override
    public String process(String text) {
        return process(text, null);
    }

    @Override
    public String process(URL url, String historyId) {
        byte[] converted = convert(url);
        String text = convertVoiceToText(converted);

        return process(text, historyId);
    }

    @Override
    public String process(byte[] bytes, String historyId) {
        String text = convertVoiceToText(bytes);

        return process(text, historyId);
    }

    @Override
    public String process(String userInput, String historyId) {
        if (!this.moderation(userInput)) {
            return this.getModeratedContent();
        }

        String history = Optional.ofNullable(historyId)
                .flatMap(id -> this.historyService
                        .getCurrent(Long.valueOf(historyId))
                        .map(this::resume))
                .orElse("");

        if (historyId == null || history.isBlank()) {
            return this.processText("userInput:" + userInput);
        }

        String prepare = String.format("resumeCurrentHistory:%s\nuserInput:%s", history, userInput);
        String result = this.processText(prepare);
        final String resume = this.resume(result);

        this.historyService.add(Long.valueOf(historyId), "{\"chatId\":\"%s\"\"userInput\":\"%s\",\"llmResponse\":\"%s\"}",
                historyId, userInput, resume);

        return result;
    }
}
