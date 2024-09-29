package com.github.aiconnection.agents.adapters.primary;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.github.aiconnection.agents.core.MessageType;
import com.github.aiconnection.agents.core.service.AgentService;
import com.github.aiconnection.agents.core.service.HistoryService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PlnAgent extends AbilityBot {
	
	private final AgentService agentService;
	private final HistoryService historyService;
	
	public PlnAgent(
			@Value("${botToken}") final String botToken, 
			@Autowired final AgentService agentService,
			@Autowired final HistoryService historyService) {
		
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
	      .action(inictAction())
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
	
	private Consumer<MessageContext> inictAction() {
		
		return messageContext-> 
			log.info("m=inictAction, chatId: {}", messageContext.chatId());
	}
	
	public Reply replyToButtons() {
		
	    final BiConsumer<BaseAbilityBot, Update> action = action();
	    
	    return Reply.of(action);
	}

	private BiConsumer<BaseAbilityBot, Update> action() {
		
		return (ability, update)-> {
			
			log.info("m=action, ability: {}, update: {}", ability, update);
			
			final MessageType messageType = identifyMessageType(update);
			
			messageType.processMessage(agentService, historyService, update, sender);
		};
	}

	private MessageType identifyMessageType(final Update update) {
		
		if(update != null && update.getMessage() != null) {
			
			final boolean text = update.getMessage().getText() != null && !"".equalsIgnoreCase(update.getMessage().getText().trim());
			final boolean hasVoice = update.getMessage().getVoice() != null;
			
			return MessageType.getMessageType(text, hasVoice);
		}
		
		return MessageType.NONE;
		
	}
}