package com.github.aiconnection.agents.core.service;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.aiconnection.agents.adapters.secondary.database.entities.HistoryEntity;
import com.github.aiconnection.agents.adapters.secondary.database.entities.HistoryEntryEntity;
import com.github.aiconnection.agents.adapters.secondary.database.repository.HistoryEntityJpaRepository;

@Service
public class DialogManagerHistoryService implements HistoryService {

	private final HistoryEntityJpaRepository historyEntityJpaRepository;

	@Autowired
	public DialogManagerHistoryService(final HistoryEntityJpaRepository historyEntityJpaRepository) {
		
		this.historyEntityJpaRepository = historyEntityJpaRepository;
	}

	@Transactional
	public String addHistory(final String uid, final String history) {
		
		return historyEntityJpaRepository.findById(uid)
				.orElse(firstHistoryEntry())
				.getHistoryEntryEntities()
				.add(HistoryEntryEntity
						.builder()
						.historyEntryId(history)
						.build()) ? history : null;
		
	}

	public String getHistory(final String uid) {
		
		return historyEntityJpaRepository.findById(uid)
				.orElse(firstHistoryEntry())
				.getHistoryEntryEntities()
				.stream()
				.map(entry->entry.getHistoryEntry())
				.collect(Collectors.joining());
		
	}

	public void clean(final String uid) {
		
		historyEntityJpaRepository.deleteById(uid);
	}

	private HistoryEntity firstHistoryEntry() {
		
		return HistoryEntity
				.builder()
				.historyEntryEntities(Arrays.asList())
				.build();
	}
}
