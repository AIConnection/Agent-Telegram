package br.senac.pos.ia.agents.inteligentes.telegram.agents;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.model.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
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

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PlnAgent extends AbilityBot {
	
	private static final String VOICE_TO_TEXT_COMMAND = 
"""
Ative o recurso de reconhecimento de fala e converta a entrada de áudio em texto. 
O sistema deve identificar corretamente as palavras faladas e apresentar o texto correspondente com alta precisão. 
A conversão deve ser realizada em tempo real e suportar múltiplos idiomas, incluindo português e inglês.
O sistema também deve ser capaz de lidar com diferentes sotaques e variações de entonação.
Além disso, o texto gerado deve ser revisado automaticamente para corrigir pequenos erros de digitação ou gramática, garantindo que o resultado final seja coerente e de fácil leitura. 
Ao final, retorne o texto convertido junto com metadados de confiança para cada palavra identificada (ex.: nível de confiança, tempo de início e término da palavra no áudio).
""";
	
	private final String botToken;
	
	private final OpenAiChatModel chatModel;
	
	public PlnAgent(@Value("${botToken}") final String botToken, @Autowired final OpenAiChatModel chatModel) {
		
		super(botToken, "PlnAgent");
		this.botToken = botToken;
		this.chatModel = chatModel;
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

	public Reply replyToButtons() {
		
	    final BiConsumer<BaseAbilityBot, Update> action = action();
	    final Predicate<Update> predicate = predicate();
	    
	    return Reply.of(action, predicate);
	}

	@Override
	public long creatorId() {
		
		log.info("m=creatorId");
		
		return 1L;
	}
	
	private Consumer<MessageContext> inictAction() {
		
		return messageContext-> {
			log.info("m=inictAction, chatId: {}", messageContext.chatId());
		};
	}
	
	private BiConsumer<BaseAbilityBot, Update> action() {
		
		return (ability, update)-> {
			
			log.info("m=action, ability: {}, update: {}", ability, update);
			
			if(voiceExist(update)) {
				final String text = voiceToText(update);
				responseMessage(text);
			}
		};
	}

	@SneakyThrows
	private void responseMessage(final String text) {
		
		sender.execute(new SendMessage("", text));		
	}

	@SneakyThrows
	private String voiceToText(final Update update) {
		
		final Voice voice = update.getMessage().getVoice();
		final File file = sender.execute(new GetFile(voice.getFileId()));
		
		final String fileUrl = file.getFileUrl(botToken);
		final UserMessage userMessage = new UserMessage(VOICE_TO_TEXT_COMMAND, List.of(new Media(MimeTypeUtils.parseMimeType(voice.getMimeType()), new URL(fileUrl))));
		
		return chatModel.call(userMessage);
	}

	private boolean voiceExist(Update update) {
		
		return update != null && update.getMessage() != null && update.getMessage().getVoice() != null;
	}
	
	private Predicate<Update> predicate() {
		
		log.info("m=predicate");
		
		return update->true;
	}
	
	@SneakyThrows
	private void mapHandle(final Long chatId) {
		
		final Map<Long, AgentState> chatStates = db.getMap("chatStates");
		final AgentState state = chatStates.get(chatId);
		
		final SendMessage message = state.action(chatId, chatStates);
		sender.execute(message);
	}
}