package com.github.aiconnection.agents.core.service.impl;

import com.github.aiconnection.agents.adapters.secondary.database.entities.HistoryEntity;
import com.github.aiconnection.agents.adapters.secondary.database.entities.HistoryEntryEntity;
import com.github.aiconnection.agents.adapters.secondary.database.repository.HistoryEntityJpaRepository;
import com.github.aiconnection.agents.adapters.secondary.database.repository.HistoryEntryEntityJpaRepository;
import com.github.aiconnection.agents.core.service.HistoryService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public void add(final Long uid, final String history, final Object... args) {

        final String historyFmt = Optional.ofNullable(args)
                .filter(ArrayUtils::isNotEmpty)
                .map(opts -> String.format(history, opts))
                .orElse(history);


        final HistoryEntryEntity historyEntryEntity = HistoryEntryEntity
                .builder()
                .historyEntry(historyFmt)
                .build();

        HistoryEntity historyEntity = historyEntityJpaRepository
                .findById(uid)
                .orElseGet(() -> historyEntityJpaRepository.save(
                        HistoryEntity
                                .builder()
                                .uid(uid)
                                .build()));

        historyEntryEntity.setHistoryEntity(historyEntity);
        historyEntryEntityJpaRepository.save(historyEntryEntity);
    }

    public Optional<String> getCurrent(final Long uid) {

        final String history = historyEntryEntityJpaRepository
                .findByHistoryEntityUid(uid, PageRequest.of(1, 3))
                .stream()
                .map(entry -> entry.getHistoryEntry())
                .collect(Collectors.joining());

        return Optional.of(history)
                .filter(StringUtils::isNotBlank);
    }

    public void clean(final Long uid) {

        historyEntityJpaRepository.deleteById(uid);
    }
}
