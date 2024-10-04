package com.github.aiconnection.agents.core.v2;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.metabot.core.bdi.BDICtx;
import org.metabot.core.bdi.BDIService;
import org.metabot.core.bdi.core.LLMInference;
import org.metabot.core.message.Message;
import org.metabot.core.message.SystemMessage;
import org.metabot.core.message.UserMessage;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Service("BDIAgent")
public class BDIAgent implements LLMInference {
    private final BDIService service;

    public BDIAgent(BDICtx ctx) {
        super();
        this.service = new BDIService(ctx);
    }

    @Override
    public String complete(final String userPrompt) {

        log.info("m=complete, userPrompt={}", userPrompt);

        return this.service.process(userPrompt);
    }

    @Override
    public String complete(Message... messages) {
        List<Message> listMessages = Optional.ofNullable(messages).map(List::of).orElse(List.of());
        final String system = listMessages.stream()
                .filter(message -> message instanceof SystemMessage)
                .map(Message::getValue)
                .collect(Collectors.joining("\n"));
        final String user = listMessages.stream()
                .filter(message -> message instanceof UserMessage)
                .map(Message::getValue)
                .collect(Collectors.joining("\n"));

        return this.service.ctx().getInference().complete(system, user);
    }


    @Override
    public String complete(final String systemPrompt, final Collection<String> prompts) {

        log.info("m=complete, systemPrompt={}, prompts={}", systemPrompt, Arrays.toString(prompts.toArray()));

        final StringBuilder builder = new StringBuilder();

        prompts.forEach(prompt -> builder.append(this.service.process(prompt)));

        return this.service.ctx().getInference().complete(systemPrompt, builder.toString());
    }

    @Override
    public String complete(final String systemPrompt, final String prompt) {

        log.info("m=complete, systemPrompt={}, prompt={}", systemPrompt, prompt);

        final String result = this.service.process(prompt);

        return this.service.ctx().getInference().complete(systemPrompt, result);
    }

}
