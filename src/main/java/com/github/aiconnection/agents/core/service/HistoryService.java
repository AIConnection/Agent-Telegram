package com.github.aiconnection.agents.core.service;

public interface HistoryService {

	void addHistory(final Long uid, final String history);

	String getHistory(final Long uid);

	void clean(final Long uid);
}
