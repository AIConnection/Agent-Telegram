package br.senac.pos.ia.agents.adapters.secundary.recognition;

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