package com.github.aiconnection.agents.adapters.secondary.recognition;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.github.aiconnection.agents.core.service.RecognitionService;

@Component("voiceRecognition")
public class VoiceRecognition implements RecognitionService {

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/audio/transcriptions";
    
    private final String groqApiToken;
    
    private final RestTemplate restTemplate;
    

    public VoiceRecognition(@Value("${spring.ai.openai.api-key}") final String groqApiToken, @Autowired final RestTemplate restTemplate) {
    	
    	this.groqApiToken = groqApiToken;
    	this.restTemplate = restTemplate;
    }

    @Override
	public String convertVoiceToText(final byte[] voiceData) {
    	
    	final ByteArrayResource resource = new ByteArrayResource(voiceData) {
    		
    		@Override
    	    public String getFilename() {
    	        return "voiceData.mp3";
    	    }
    	};

        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + groqApiToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        final MultiValueMap<String, Object> multipartBody = new LinkedMultiValueMap<>();
        
        multipartBody.add("file", resource);
        multipartBody.add("model", "whisper-large-v3");
        multipartBody.add("temperature", "0");
        multipartBody.add("response_format", "json");
        multipartBody.add("language", "pt");

        final HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartBody, headers);
        final ResponseEntity<GroqResponse> groqResponseEntity = restTemplate.exchange(GROQ_API_URL, HttpMethod.POST, requestEntity, GroqResponse.class);
        final GroqResponse groqResponse = groqResponseEntity.getBody();
        
        return Optional.ofNullable(groqResponse != null ? groqResponse.getText() : null).orElseThrow();
    }
}
