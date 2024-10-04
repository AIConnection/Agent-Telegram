package com.github.aiconnection.agents.v2;

import lombok.extern.slf4j.Slf4j;
import org.metabot.core.bdi.core.LLMInference;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("openAI")
public class OpenAiChatAgent implements LLMInference {
    private final OpenAiChatModel openAiChatModel;

    public OpenAiChatAgent(@Autowired final OpenAiChatModel openAiChatModel) {
        this.openAiChatModel = openAiChatModel;
    }

    @Override
    public String complete(final String systemPrompt, final String userPrompt) {

        log.info("m=complete, systemPrompt={}, userPrompt={}", systemPrompt, userPrompt);

        return complete(new Prompt(Arrays.asList(new SystemMessage(systemPrompt), new UserMessage(userPrompt))));
    }

    @Override
    public String complete(final String systemPrompt, final Collection<String> prompts) {

        log.info("m=complete, systemPrompt={}, prompts={}", systemPrompt, Arrays.toString(prompts.toArray()));

        final StringBuilder builder = new StringBuilder();
        prompts.forEach(builder::append);

        final ArrayList<Message> bdiMessages = new ArrayList<>();

        bdiMessages.add(new SystemMessage(systemPrompt));
        bdiMessages.add(new UserMessage(builder.toString()));

        return complete(new Prompt(bdiMessages));
    }

    @Override
    public String complete(String userPrompt) {

        log.info("m=complete, userPrompt={}", userPrompt);

        final Prompt prompt = new Prompt(userPrompt);

        return complete(prompt);
    }

    @Override
    public String complete(org.metabot.core.message.Message... messages) {
        List<org.metabot.core.message.Message> listMessages = Optional.ofNullable(messages)
                .map(List::of)
                .orElseGet(List::of);
        final List<Message> system = listMessages.stream()
                .filter(message -> message instanceof SystemMessage)
                .map(org.metabot.core.message.Message::getValue)
                .map(SystemMessage::new)
                .collect(Collectors.toList());
        final List<Message> user = listMessages.stream()
                .filter(message -> message instanceof UserMessage)
                .map(org.metabot.core.message.Message::getValue)
                .map(UserMessage::new)
                .collect(Collectors.toList());

        system.addAll(user);
        return complete(new Prompt(system));
    }

    private String complete(final Prompt prompt) {

        final ChatResponse chatResponse = openAiChatModel.call(prompt);

        return chatResponse
                .getResult()
                .getOutput()
                .getContent();
    }

}
