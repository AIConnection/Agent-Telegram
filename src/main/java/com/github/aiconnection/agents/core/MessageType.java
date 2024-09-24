package com.github.aiconnection.agents.core;

import java.net.URL;

import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Voice;

import com.github.aiconnection.agents.core.service.AgentService;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum MessageType {
	TEXT {
		@Override
		public void processMessage(final AgentService agentService, final Update update, final MessageSender sender) {
			
			log.info("m=processMessage msg='MessageType.TEXT activate service, send TEXT'");
			
			final Long chatId = update.getMessage().getChatId();
			final String message = update.getMessage().getText();
			final String text = agentService.processText(message);
			
			responseMessage(chatId, text, sender);
		}
	},
	VOICE {
		@Override
		@SneakyThrows
		public void processMessage(final AgentService agentService, final Update update, final MessageSender sender) {
			
			log.info("m=processMessage msg='MessageType.VOICE activate service, send VOICE'");
			
			final Long chatId = update.getMessage().getChatId();
			final Voice voice = update.getMessage().getVoice();
			final File file = sender.execute(new GetFile(voice.getFileId()));
			final String fileUrl = file.getFileUrl(agentService.getBotToken());
			final String text = agentService.processVoice(new URL(fileUrl));
			
			responseMessage(chatId, text, sender);
		}
	},
	TEXT_AND_VOICE {
		@Override
		public void processMessage(final AgentService agentService, final Update update, final MessageSender sender) {
			
			log.info("m=processMessage msg='MessageType.TEXT_AND_VOICE activate service, send TEXT AND VOICE'");
			
			MessageType.TEXT.processMessage(agentService, update, sender);
			MessageType.VOICE.processMessage(agentService, update, sender);
		}
	},
	NONE {
		@Override
		public void processMessage(final AgentService agentService, final Update update, final MessageSender sender) {
			
			log.info("m=processMessage msg='MessageType.NONE activate service, not implementation.'");
		}
	};
	
	@SneakyThrows
	public static void responseMessage(final Long chatId, final String text, final MessageSender sender) {
		
		final SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
	    sender.execute(sendMessage); 
	}

	public static MessageType getMessageType(boolean hasText, boolean hasVoice) {

		if (hasText && hasVoice) {

			return TEXT_AND_VOICE;

		} else if (hasText) {

			return TEXT;

		} else if (hasVoice) {

			return VOICE;
		}

		return NONE;
	}

	public abstract void processMessage(final AgentService agentService, final Update update, final MessageSender sender);
}
