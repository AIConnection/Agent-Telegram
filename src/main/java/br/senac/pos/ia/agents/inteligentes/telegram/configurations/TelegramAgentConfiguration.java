package br.senac.pos.ia.agents.inteligentes.telegram.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramAgentConfiguration {

	private final AbilityBot plnAgent;
	
	public TelegramAgentConfiguration(@Autowired @Qualifier("plnAgent") final AbilityBot plnAgent) {
		this.plnAgent = plnAgent;
	}
	
	@Bean
	public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
		
		 final TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
         botsApi.registerBot(plnAgent);
         
         return botsApi;
	}
}
