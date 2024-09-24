package com.github.aiconnection.agents.adapters.secondary.recognition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class GroqResponse {
	
    private String text;
    private XGroq xGroq;
}    