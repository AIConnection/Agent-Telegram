package com.github.connectionai.agents.adapters.secondary.conversion;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.github.connectionai.agents.core.service.ConversionService;

import lombok.SneakyThrows;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

@Component
public class AudioConversion implements ConversionService {

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
}