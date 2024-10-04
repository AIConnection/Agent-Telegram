package com.github.aiconnection.agents.core.v2;

import com.github.aiconnection.agents.core.service.AgentService;
import org.metabot.core.bdi.BDICtx;
import org.metabot.core.bdi.core.LLMInference;
import org.metabot.core.bdi.repo.BDIRepo;
import org.metabot.core.media.Media;
import org.metabot.core.media.MediaTranscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;

import javax.activation.MimetypesFileTypeMap;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;

@Service("telegramAgent")
public class TelegramAgent implements AgentService {
    private final String telegramToken;
    private final BDIAgent agent;
    private final MediaTranscription<ByteBuffer> audioToMp3;
    private final MediaTranscription<String> mp3ToText;

    @Autowired
    public TelegramAgent(
            @Value("${botToken}") final String telegramToken,
            @Qualifier("jsonBDI") final BDIRepo repo,
            @Qualifier("openAI") LLMInference inference,
            @Qualifier("audioConversion") MediaTranscription<ByteBuffer> audioToMp3,
            @Qualifier("voiceRecognition") MediaTranscription<String> mp3ToText) {
        this.telegramToken = telegramToken;
        this.audioToMp3 = audioToMp3;
        this.mp3ToText = mp3ToText;
        this.agent = new BDIAgent(new BDICtx(repo, inference));
    }

    @Override
    public String getBotToken() {
        return this.telegramToken;
    }

    @Override
    public String resume(String text) {
        return this.agent.getService().resume(text);
    }

    @Override
    public boolean moderation(String userInput) {
        return this.agent.getService().moderation(userInput);
    }

    @Override
    public String getModeratedContent() {
        return " Parece que sua mensagem não está relacionada com o tipo de serviço que eu ofereço.";
    }

    @Override
    public byte[] convert(URL sourceFile) {
        String contentType = new MimetypesFileTypeMap().getContentType(sourceFile.getFile());
        Media media = new Media(MimeType.valueOf(contentType), sourceFile);

        return this.audioToMp3.transcript(media)
                .getValue()
                .array();
    }

    @Override
    public String convertVoiceToText(byte[] voiceData) {
        ByteArrayResource resource = new ByteArrayResource(voiceData);
        Media media = new Media(MimeType.valueOf("audio/mpeg"), resource);

        return this.mp3ToText.transcript(media).getValue();
    }

    @Override
    public String complete(String systemPrompt, String userPrompt, List<String> prompts) {
        Assert.notNull(systemPrompt, "SystemPrompt must not be null");
        Assert.notNull(userPrompt, "UserPrompt must not be null");
        Assert.notNull(prompts, "Prompts must not be null");

        prompts.add(userPrompt);
        return this.agent.complete(systemPrompt, prompts);
    }

    @Override
    public String complete(String systemPrompt, String prompt) {
        return this.agent.complete(systemPrompt, prompt);
    }

    @Override
    public String complete(String prompt) {
        return this.agent.complete(prompt);
    }

}
