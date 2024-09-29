package com.github.aiconnection.agents.core.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.aiconnection.agents.adapters.secondary.database.entities.HistoryEntity;
import com.github.aiconnection.agents.adapters.secondary.database.entities.HistoryEntryEntity;
import com.github.aiconnection.agents.adapters.secondary.database.repository.HistoryEntityJpaRepository;
import com.github.aiconnection.agents.adapters.secondary.database.repository.HistoryEntryEntityJpaRepository;

@Service
public class DialogManagerHistoryService implements HistoryService {

	private final HistoryEntityJpaRepository historyEntityJpaRepository;
	private final HistoryEntryEntityJpaRepository historyEntryEntityJpaRepository;

	@Autowired
	public DialogManagerHistoryService(
			final HistoryEntityJpaRepository historyEntityJpaRepository,
			final HistoryEntryEntityJpaRepository historyEntryEntityJpaRepository) {
		
		this.historyEntityJpaRepository = historyEntityJpaRepository;
		this.historyEntryEntityJpaRepository = historyEntryEntityJpaRepository;
	}

	@Transactional
	public void addHistory(final Long uid, final String history) {
		
		final HistoryEntryEntity historyEntryEntity = HistoryEntryEntity
														.builder()
														.historyEntry(history)
														.build();
		
		historyEntityJpaRepository.findById(uid).ifPresentOrElse(historyEntity->{
			
			historyEntryEntity.setHistoryEntity(historyEntity);
			
			historyEntryEntityJpaRepository.save(historyEntryEntity);
			
		}, ()->{
			
			final HistoryEntity historyEntity = historyEntityJpaRepository.save(HistoryEntity
					.builder()
					.uid(uid)
					.build());
			
			historyEntryEntity.setHistoryEntity(historyEntity);
			
			historyEntryEntityJpaRepository.save(historyEntryEntity);
		});	
	}

	public String getHistory(final Long uid) {
		
		return historyEntryEntityJpaRepository.findByHistoryEntityUid(uid, PageRequest.of(1, 3))
				.stream()
				.map(entry->entry.getHistoryEntry())
				.collect(Collectors.joining());
	}

	public void clean(final Long uid) {
		
		historyEntityJpaRepository.deleteById(uid);
	}
}
