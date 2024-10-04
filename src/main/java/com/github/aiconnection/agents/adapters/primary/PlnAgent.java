package com.github.aiconnection.agents.adapters.primary;

import com.github.aiconnection.agents.core.MessageType;
import com.github.aiconnection.agents.core.service.AgentService;
import com.github.aiconnection.agents.core.service.HistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Slf4j
@Service
public class PlnAgent extends AbilityBot {

    private final AgentService agentService;
    private final HistoryService historyService;

    @Autowired
    public PlnAgent(
            @Value("${botToken}") final String botToken,
            @Qualifier("telegramAgent") final AgentService agentService,
//            @Qualifier("intelligentAgentService") final AgentService agentService,
            final HistoryService historyService) {

        super(botToken, "PlnAgent");

        this.agentService = agentService;
        this.historyService = historyService;
    }

    public Ability startBot() {

        return Ability
                .builder()
                .name("start")
                .info("START")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(initAction())
                .build();
    }

    public Ability handleTextMessages() {

        return Ability
                .builder()
                .name(DEFAULT)
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    final String text = ctx.update().getMessage().getText();
                    final String response = agentService.complete(text);

                    MessageType.responseMessage(ctx.update().getMessage().getChatId(), response, sender);
                })
                .build();
    }

    @Override
    public long creatorId() {

        log.info("m=creatorId");

        return 1L;
    }

    private Consumer<MessageContext> initAction() {

        return messageContext ->
                log.info("m=inictAction, chatId: {}", messageContext.chatId());
    }

    public Reply replyToButtons() {

        final BiConsumer<BaseAbilityBot, Update> action = action();

        return Reply.of(action);
    }

    private BiConsumer<BaseAbilityBot, Update> action() {

        return (ability, update) -> {

            log.info("m=action, ability: {}, update: {}", ability, update);

            final MessageType messageType = identifyMessageType(update);

            messageType.processMessage(agentService, historyService, update, sender);
        };
    }

    private MessageType identifyMessageType(final Update update) {
        return Optional.ofNullable(update)
                .filter(u -> update.getMessage() != null)
                .map((u) -> {
                    final boolean hasVoice = Optional.ofNullable(u.getMessage().getVoice())
                            .isPresent();
                    final boolean hasText = Optional.ofNullable(u.getMessage().getText())
                            .filter(txt -> !"".equalsIgnoreCase(txt.trim()))
                            .filter(txt -> !txt.substring(0, 1).equals("/"))
                            .isPresent();
                    final boolean hasCommand = Optional.ofNullable(u.getMessage().getText())
                            .filter(txt -> !"".equalsIgnoreCase(txt.trim()))
                            .filter(txt -> txt.substring(0, 1).equals("/"))
                            .isPresent();

                    return MessageType.getMessageType(hasText, hasVoice, hasCommand);
                })
                .orElse(MessageType.NONE);
    }
}