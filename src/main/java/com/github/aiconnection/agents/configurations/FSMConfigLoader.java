package com.github.aiconnection.agents.configurations;

import java.io.File;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.aiconnection.agents.core.fsm.FSM;

@Configuration
public class FSMConfigLoader {

	@Bean
	public FSM fsm() throws IOException {
        
		final String filePath = "bdi/FSM.json";
		
		final ObjectMapper objectMapper = new ObjectMapper();
        final File configFile = new File(filePath);
        
        return objectMapper.readValue(configFile, FSM.class);
    }
}
