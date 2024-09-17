package br.senac.pos.ia.agents.inteligentes.telegram.contracts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroqResponse {
	
    private String text;
    private XGroq xGroq;
}    