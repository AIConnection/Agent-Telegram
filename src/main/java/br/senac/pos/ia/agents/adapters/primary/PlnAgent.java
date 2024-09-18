package br.senac.pos.ia.agents.adapters.primary;

import java.net.URL;
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
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Voice;

import br.senac.pos.ia.agents.service.AgentService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PlnAgent extends AbilityBot {
	
	private final String botToken;
	
	private final AgentService agentService;
	
	public PlnAgent(@Value("${botToken}") final String botToken, @Autowired final AgentService agentService) {
		
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
	            responseMessage(ctx.update().getMessage().getChatId(), response);
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
			
			if(voiceExist(update)) {
				
				log.info("m=action, voiceExist: {}", true);
				
				final String text = voiceToText(update);
				
				log.info("m=action, llmResponse: {}", text);
				
				responseMessage(update.getMessage().getChatId(), text);
			}
		};
	}

	@SneakyThrows
	private void responseMessage(final Long chatId, final String text) {
		
		final SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
	    sender.execute(sendMessage); 
	}
	
	private boolean voiceExist(final Update update) {
		
		return update != null && update.getMessage() != null && update.getMessage().getVoice() != null;
	}
	
	@SneakyThrows
	private String voiceToText(final Update update) {
		
		final Voice voice = update.getMessage().getVoice();
		final File file = sender.execute(new GetFile(voice.getFileId()));
		final String fileUrl = file.getFileUrl(botToken);
		
		return agentService.processVoice(new URL(fileUrl));
	}
}