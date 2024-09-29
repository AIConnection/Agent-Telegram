package com.github.aiconnection.agents.adapters.secondary.database.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.github.aiconnection.agents.adapters.secondary.database.entities.HistoryEntryEntity;

public interface HistoryEntryEntityJpaRepository extends JpaRepository<HistoryEntryEntity, Long>{

	@Query("select he from HistoryEntryEntity he where he.historyEntity.uid=:uid order by he.historyEntryId desc")
	List<HistoryEntryEntity> findByHistoryEntityUid(@Param("uid") final Long uid, Pageable pageable);

}
