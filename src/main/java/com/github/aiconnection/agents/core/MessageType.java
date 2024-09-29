package com.github.aiconnection.agents.core;

import java.net.URL;

import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Voice;

import com.github.aiconnection.agents.core.service.AgentService;
import com.github.aiconnection.agents.core.service.HistoryService;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum MessageType {
	TEXT {
		@Override
		public void processMessage(final AgentService agentService, final HistoryService historyService,
				final Update update, final MessageSender sender) {

			final Long chatId = update.getMessage().getChatId();

			final String userInput = update.getMessage().getText();

			log.info("m=processMessage, chatId={}, userInput={},  msg='MessageType.TEXT activate service, send TEXT'",
					chatId, userInput);

			try {
				
				if(!agentService.moderation(userInput)) {
					responseMessage(chatId, agentService.getModeratedContent(), sender);
					return;
				}

				final String currentHistory = MessageType.getCurrentHistory(historyService, chatId);

				if (currentHistory != null && !"".equals(currentHistory)) {

					final String resumeCurrentHistory = agentService.resume(currentHistory);

					log.debug("m=processMessage, userInput={}, resumeCurrentHistory={}", userInput,
							resumeCurrentHistory);

					final String text = agentService.processText(String.format("resumeCurrentHistory:%s%suserInput:%s",
							resumeCurrentHistory, "\n", userInput));

					log.debug("m=processMessage, userInput={}, text={}", userInput, text);

					responseMessage(chatId, text, sender);

					final String resume = agentService.resume(text);

					MessageType.addHistory(historyService, chatId, userInput, resume);

				} else {

					log.debug("m=processMessage, userInput={}", userInput);

					final String text = agentService.processText(String.format("userInput:%s", userInput));

					log.debug("m=processMessage, userInput={}, text={}", userInput, text);

					responseMessage(chatId, text, sender);

					final String resume = agentService.resume(text);

					MessageType.addHistory(historyService, chatId, userInput, resume);
				}

			} catch (Exception e) {

				log.error("m=processMessage, update: {}", update, e);

				responseMessage(chatId, "teste novamente mais tarde.", sender);
			}
		}
	},
	VOICE {
		@Override
		@SneakyThrows
		public void processMessage(final AgentService agentService, final HistoryService historyService,
				final Update update, final MessageSender sender) {

			log.info("m=processMessage msg='MessageType.VOICE activate service, send VOICE'");

			final Long chatId = update.getMessage().getChatId();

			final Voice voice = update.getMessage().getVoice();

			final File file = sender.execute(new GetFile(voice.getFileId()));

			final String fileUrl = file.getFileUrl(agentService.getBotToken());

			final String userInput = agentService.convertVoiceToText(new URL(fileUrl));

			final String text = agentService.processText(userInput);

			responseMessage(chatId, text, sender);
		}
	},
	TEXT_AND_VOICE {
		@Override
		public void processMessage(final AgentService agentService, final HistoryService historyService,
				final Update update, final MessageSender sender) {

			log.info("m=processMessage msg='MessageType.TEXT_AND_VOICE activate service, send TEXT AND VOICE'");

			MessageType.TEXT.processMessage(agentService, historyService, update, sender);
			MessageType.VOICE.processMessage(agentService, historyService, update, sender);
		}
	},
	COMMAND {

		@Override
		public void processMessage(final AgentService agentService, final HistoryService historyService,
				final Update update, final MessageSender sender) {

			final Long chatId = update.getMessage().getChatId();

			final String userInput = update.getMessage().getText();

			if ("/reset".equalsIgnoreCase(userInput.trim())) {
				historyService.clean(chatId);
			} else if ("/debug".equalsIgnoreCase(userInput.trim())) {

				// TODO: implement explainability mechanism. The idea is to generate a response
				// with a step-by-step guide to what the agent is doing..
				log.info("implementar explicabilidade.");
			}
		}
	},
	NONE {
		@Override
		public void processMessage(final AgentService agentService, final HistoryService historyService,
				final Update update, final MessageSender sender) {

			log.info("m=processMessage msg='MessageType.NONE activate service, not implementation.'");
		}
	};

	@SneakyThrows
	public static void responseMessage(final Long chatId, final String text, final MessageSender sender) {

		final SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);

		sender.execute(sendMessage);
	}

	public static MessageType getMessageType(boolean hasText, boolean hasVoice, boolean hasCommand) {

		if (hasText && hasVoice) {

			return TEXT_AND_VOICE;

		} else if (hasText) {

			return TEXT;

		} else if (hasVoice) {

			return VOICE;

		} else if (hasCommand) {

		}

		return NONE;
	}

	public abstract void processMessage(final AgentService agentService, final HistoryService historyService,
			final Update update, final MessageSender sender);

	protected static void addHistory(final HistoryService historyService, final Long chatId, final String userInput,
			final String text) {

		historyService.addHistory(chatId, String
				.format("{\"chatId\":\"%s\"\"userInput\":\"%s\",\"llmResponse\":\"%s\"}", chatId, userInput, text));
	}

	protected static String getCurrentHistory(final HistoryService historyService, final Long chatId) {

		return historyService.getHistory(chatId);
	}
}
