package com.github.aiconnection.agents.v2;

import lombok.Getter;
import org.metabot.core.bdi.BDIAgent;
import org.metabot.core.bdi.core.LLMInference;
import org.metabot.core.bdi.repo.BDIRepo;
import org.metabot.core.media.MediaTranscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Optional;

@Getter
@Service("telegramAgent")
public class TelegramAgent extends BDIAgent {

    @Autowired
    public TelegramAgent(
            @Qualifier("jsonBDI") final BDIRepo repo,
            @Qualifier("openAI") LLMInference inference,
            @Qualifier("audioConversion") MediaTranscription<ByteBuffer> audioToMp3,
            @Qualifier("voiceRecognition") MediaTranscription<String> mp3ToText) {

        super(repo, inference, audioToMp3, mp3ToText);
    }

    public static void responseMessage(final String chatId, final String text, final MessageSender sender) throws TelegramApiException {
        final SendMessage sendMessage = new SendMessage(chatId, text);
        sender.execute(sendMessage);
    }

    public static URL toURL(String token, MessageSender sender, Voice voice) throws TelegramApiException, MalformedURLException {
        final File file = sender.execute(new GetFile(voice.getFileId()));
        final String fileUrl = file.getFileUrl(token);

        return new URL(fileUrl);
    }

    public static MessageType getMessageType(final Update update) {
        return Optional
                .ofNullable(update)
                .filter(u -> update.getMessage() != null)
                .map((u) -> {
                    final boolean hasVoice = Optional.ofNullable(u.getMessage().getVoice())
                            .isPresent();
                    final boolean hasText = Optional.ofNullable(u.getMessage().getText())
                            .filter(txt -> !"".equalsIgnoreCase(txt.trim()))
                            .filter(txt -> txt.charAt(0) != '/')
                            .isPresent();
                    final boolean hasCommand = Optional.ofNullable(u.getMessage().getText())
                            .filter(txt -> !"".equalsIgnoreCase(txt.trim()))
                            .filter(txt -> txt.charAt(0) == '/')
                            .isPresent();

                    return MessageType.valueOf(hasText, hasVoice, hasCommand);
                })
                .orElse(MessageType.NONE);
    }

    public enum MessageType {
        TEXT_AND_VOICE,
        TEXT,
        VOICE,
        COMMAND,
        NONE;

        static MessageType valueOf(boolean hasText, boolean hasVoice, boolean hasCommand) {
            if (hasText && hasVoice) {
                return MessageType.TEXT_AND_VOICE;
            } else if (hasText) {
                return MessageType.TEXT;
            } else if (hasVoice) {
                return MessageType.VOICE;
            } else if (hasCommand) {
                return MessageType.COMMAND;
            }

            return MessageType.NONE;
        }
    }
}
