package com.github.aiconnection.agents.adapters.primary;

import com.github.aiconnection.agents.v2.TelegramAgent;
import lombok.extern.slf4j.Slf4j;
import org.metabot.core.bdi.core.Agent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Slf4j
@Service
public class PlnAgent extends AbilityBot {

    private final String botToken;
    private final Agent agentService;

    @Autowired
    public PlnAgent(
            @Value("${botToken}") String botToken,
            @Qualifier("telegramAgent") Agent agentService
//            @Qualifier("intelligentAgentService") final Agent agentService
    ) {

        super(botToken, "PlnAgent");
        this.botToken = botToken;
        this.agentService = agentService;
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
                .action(ctx -> this.actionPerform(ctx.update()))
                .build();
    }

    private void actionPerform(Update update) {
        final String chatId = update.getMessage().getChatId().toString();

        try {
            final TelegramAgent.MessageType messageType = TelegramAgent.getMessageType(update);
            switch (messageType) {
                case TEXT:
                    processText(update);
                    break;
                case VOICE:
                    processVoice(update);
                    break;
                case TEXT_AND_VOICE:
                    processText(update);
                    processVoice(update);
                    break;
            }
        } catch (Exception e) {
            try {
                log.error("m=processMessage, update: {}", update, e);
                TelegramAgent.responseMessage(chatId, "Teste novamente mais tarde.", sender);
            } catch (Exception critical) {
                log.error("m=processMessage, update: {}", update, critical);
            }
        }
    }

    @Override
    public long creatorId() {
        log.info("m=creatorId");
        return UUID.randomUUID().getMostSignificantBits();
    }

    private void processText(Update update) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        String text = update.getMessage().getText();
        String result = this.agentService.process(text, chatId);

        TelegramAgent.responseMessage(chatId, result, sender);
    }

    private void processVoice(Update update) throws TelegramApiException, MalformedURLException {
        String chatId = update.getMessage().getChatId().toString();
        URL url = TelegramAgent.toURL(botToken, sender, update.getMessage().getVoice());
        String result = this.agentService.process(url, chatId);

        TelegramAgent.responseMessage(chatId, result, sender);
    }

    public Reply replyToButtons() {

        final BiConsumer<BaseAbilityBot, Update> action = action();

        return Reply.of(action);
    }

    private Consumer<MessageContext> initAction() {

        return messageContext ->
                log.info("m=inictAction, chatId: {}", messageContext.chatId());
    }

    private BiConsumer<BaseAbilityBot, Update> action() {

        return (ability, update) -> {

            log.info("m=action, ability: {}, update: {}", ability, update);

            this.actionPerform(update);
        };
    }

}