package com.github.aiconnection.agents.adapters.secondary.database.entities;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistoryEntity implements Serializable {

	private static final long serialVersionUID = 8430857046738628925L;

	@Id
	private String uid;
	
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "uid")
	private List<HistoryEntryEntity> historyEntryEntities;
}
