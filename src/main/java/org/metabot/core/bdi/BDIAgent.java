package org.metabot.core.bdi;

import org.metabot.core.bdi.core.Agent;
import org.metabot.core.bdi.core.LLMInference;
import org.metabot.core.bdi.repo.BDIRepo;
import org.metabot.core.media.Media;
import org.metabot.core.media.MediaTranscription;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.MimeType;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Optional;

public class BDIAgent implements Agent {
    private final MediaTranscription<ByteBuffer> audioToMp3;
    private final MediaTranscription<String> mp3ToText;
    private final BDIService service;

    public BDIAgent(
            BDIRepo repo,
            LLMInference inference,
            MediaTranscription<ByteBuffer> audioToMp3,
            MediaTranscription<String> mp3ToText) {
        super();
        this.audioToMp3 = audioToMp3;
        this.mp3ToText = mp3ToText;
        this.service = new BDIService(new BDICtx(repo, inference));
    }

    public String process(final URL audio) {
        return process(audio, null);
    }

    public String process(final byte[] bytes) {
        return process(bytes, null);
    }

    public String process(final String text) {
        return process(text, null);
    }

    public String process(final URL audio, final String historyId) {
    	final Media media = new Media(MimeType.valueOf("audio/ogg"), audio);
    	final ByteBuffer transcript = audioToMp3.transcript(media).getValue();

        return process(transcript.array(), historyId);
    }

    public String process(final byte[] bytes, final String historyId) {
    	final ByteArrayResource resource = new ByteArrayResource(bytes);
    	final Media media = new Media(MimeType.valueOf("audio/mpeg"), resource);
    	final String text = mp3ToText.transcript(media).getValue();

        return this.process(text, historyId);
    }

    public String process(final String userInput, final String historyId) {
        if (!this.service.moderation(userInput)) {
            return this.service.getModerateContent();
        }

        final String history = Optional.ofNullable(historyId)
                .flatMap(id -> this.service
                        .ctx()
                        .getRepo()
                        .getHistory(historyId)
                        .map(this.service::resume))
                .orElse("");

        if (history.isBlank()) {
            return this.processText("userInput:" + userInput);
        }

        final String prepare = String.format("resumeCurrentHistory:%s%suserInput:%s", history, "\n", userInput);
        final String result = this.processText(prepare);
        final String resume = this.service.resume(result);

        this.service.ctx()
                .getRepo()
                .addHistory(historyId, "{\"chatId\":\"%s\"\"userInput\":\"%s\",\"llmResponse\":\"%s\"}",
                        historyId, userInput, resume);

        return result;
    }

    private String processText(final String text) {
        return this.service.process(text);
    }

}