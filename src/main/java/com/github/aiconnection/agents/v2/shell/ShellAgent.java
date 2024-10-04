package com.github.aiconnection.agents.v2.shell;

import org.jline.utils.AttributedString;
import org.metabot.core.bdi.BDIAgent;
import org.metabot.core.bdi.core.LLMInference;
import org.metabot.core.bdi.repo.BDIRepo;
import org.metabot.core.media.MediaTranscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

@Service("shellAgent")
public class ShellAgent extends BDIAgent {

    @Autowired
    public ShellAgent(
            @Qualifier("jsonBDI") final BDIRepo repo,
            @Qualifier("openAI") LLMInference inference,
            @Qualifier("audioConversion") MediaTranscription<ByteBuffer> audioToMp3,
            @Qualifier("voiceRecognition") MediaTranscription<String> mp3ToText) {

        super(repo, inference, audioToMp3, mp3ToText);
    }

    @Bean
    PromptProvider promptProvider() {
        return () -> new AttributedString("METABOT:>");
    }
}
