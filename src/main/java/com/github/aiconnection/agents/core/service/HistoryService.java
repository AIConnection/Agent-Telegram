package com.github.aiconnection.agents.core.service;

public interface HistoryService {

	String addHistory(final String uid, final String history);

	String getHistory(final String uid);

	void clean(final String uid);
}
