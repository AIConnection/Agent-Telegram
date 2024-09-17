package br.senac.pos.ia.agents.inteligentes.telegram.agents;

import java.net.URL;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.springframework.ai.openai.OpenAiChatModel;
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

import br.senac.pos.ia.agents.inteligentes.telegram.tools.AudioConversion;
import br.senac.pos.ia.agents.inteligentes.telegram.tools.VoiceRecognition;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PlnAgent extends AbilityBot {
	
	private final String botToken;
	
	private final OpenAiChatModel chatModel;
	
	private final VoiceRecognition voiceRecognitionService;
	
	private final AudioConversion audioConversion;
	
	public PlnAgent(
			@Value("${botToken}") final String botToken,
			@Autowired final OpenAiChatModel chatModel,
			@Autowired final VoiceRecognition voiceRecognitionService,
			@Autowired final AudioConversion audioConversion) {
		
		super(botToken, "PlnAgent");
		this.botToken = botToken;
		this.chatModel = chatModel;
		this.voiceRecognitionService = voiceRecognitionService;
		this.audioConversion = audioConversion;
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
	            final String userMessage = ctx.update().getMessage().getText();
	            final String response = chatModel.call(userMessage);
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
	private String voiceToText(final Update update) {
		
		try {
			final Voice voice = update.getMessage().getVoice();
			final File file = sender.execute(new GetFile(voice.getFileId()));
			final String fileUrl = file.getFileUrl(botToken);
			
			//https://www.ffmpeg.org/download.html
			final byte[] mp3 = audioConversion.convertOggToMp3(new URL(fileUrl));
			
			log.info("m=voiceToText, mp3.length: {} bytes", mp3.length);
			
			final String text = voiceRecognitionService.convertVoiceToText(mp3);
			
			log.info("m=voiceToText, text: {}", text);
			
			return chatModel.call(text);
		}catch (Exception e) {
			
			log.error("m=voiceToText", e);
			
			throw new RuntimeException(e);
		}
	}

	@SneakyThrows
	private void responseMessage(final Long chatId, final String text) {
	    sender.execute(new SendMessage(String.valueOf(chatId), text));        
	}
	
	private boolean voiceExist(Update update) {
		
		return update != null && update.getMessage() != null && update.getMessage().getVoice() != null;
	}
}