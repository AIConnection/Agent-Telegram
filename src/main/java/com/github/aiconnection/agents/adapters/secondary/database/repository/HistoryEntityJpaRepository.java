package com.github.aiconnection.agents.adapters.secondary.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.aiconnection.agents.adapters.secondary.database.entities.HistoryEntity;

@Repository
public interface HistoryEntityJpaRepository extends JpaRepository<HistoryEntity, Long>{

}
