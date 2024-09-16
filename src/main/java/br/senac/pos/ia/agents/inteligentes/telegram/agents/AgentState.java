package br.senac.pos.ia.agents.inteligentes.telegram.agents;

import java.util.Map;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import lombok.SneakyThrows;

public enum AgentState {
    AWAITING_NAME {
		@Override
		public SendMessage action(final Long chatId, final Map<Long, AgentState> chatStates) {
			
			return SendMessage.builder().build();
		}
	},
    AWAITING_CONFIRMATION {
		@Override
		public SendMessage action(final Long chatId, final Map<Long, AgentState> chatStates) {
			
			return SendMessage.builder().build();
		}
	},
    STOP {
		@Override
		@SneakyThrows
		public SendMessage action(final Long chatId, final Map<Long, AgentState> chatStates) {
			
		    chatStates.remove(chatId);
		    
		    return SendMessage
		    		.builder()
		    		.chatId(chatId)
		    		.text("Até logo!")
		    		.replyMarkup(new ReplyKeyboardRemove(true))
		    		.build();
		}
	};
    
    public abstract SendMessage action(final Long chatId, final Map<Long, AgentState> chatStates);
}