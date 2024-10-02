package com.github.aiconnection.agents.core.service;

import java.util.Optional;

public interface HistoryService {

	Optional<String> getCurrent(final Long uid);

	void add(final Long uid, final String history, Object ...opts);

	void clean(final Long uid);
}
