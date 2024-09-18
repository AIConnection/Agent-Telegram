package br.senac.pos.ia.agents.configurations;

import java.nio.file.FileSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.google.common.jimfs.Jimfs;

@Configuration
public class AgentConfiguration {

	private final AbilityBot plnAgent;
	
	public AgentConfiguration(@Autowired @Qualifier("plnAgent") final AbilityBot plnAgent) {
		this.plnAgent = plnAgent;
	}
	
	@Bean
	public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
		
		 final TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
         botsApi.registerBot(plnAgent);
         
         return botsApi;
	}
	
	@Bean
	public FileSystem inMemoryFileSystem() {
		return Jimfs.newFileSystem(com.google.common.jimfs.Configuration.unix());
	}
}
