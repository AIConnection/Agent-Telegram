package com.github.aiconnection.agents.adapters.secondary.conversion;

import com.github.aiconnection.agents.core.service.ConversionService;
import lombok.SneakyThrows;
import org.metabot.core.bdi.core.Content;
import org.metabot.core.media.Media;
import org.metabot.core.media.MediaTranscription;
import org.springframework.stereotype.Component;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Component("audioConversion")
public class AudioConversion implements ConversionService, MediaTranscription<ByteBuffer> {

    @Override
    @SneakyThrows
    public byte[] convert(final URL sourceFile) {

        // Define audio attributes for MP3 conversion
        final AudioAttributes audio = new AudioAttributes();
        audio.setCodec("libmp3lame");
        audio.setBitRate(16000);
        audio.setChannels(1);
        audio.setSamplingRate(44100);

        // Define encoding attributes
        final EncodingAttributes attrs = new EncodingAttributes();
        attrs.setOutputFormat("mp3");
        attrs.setAudioAttributes(audio);

        // Perform the conversion from OGG to MP3
        final Path targetMp3File = Files.createTempFile("agent-", "-ai");
        final Encoder encoder = new Encoder();
        encoder.encode(new MultimediaObject(sourceFile), targetMp3File.toFile(), attrs);

        return Files.readAllBytes(targetMp3File);
    }

    @Override
    public Content<ByteBuffer, String> transcript(Media media) {
        byte[] converted = this.convert((URL) media.getData());

        return new Content<>() {
            private ByteBuffer buffer = ByteBuffer.wrap(converted);

            @Override
            public ByteBuffer getValue() {
                return buffer;
            }

            @Override
            public Map<String, String> getMetadata() {
                return Map.of();
            }
        };
    }
}