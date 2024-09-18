package br.senac.pos.ia.agents.service;

public interface Recognition {

	String convertVoiceToText(final byte[] voiceData);

}