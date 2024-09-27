package com.github.aiconnection.agents.adapters.secondary.database.entities;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistoryEntryEntity implements Serializable{

	private static final long serialVersionUID = -5384084234291371646L;

	@Id
	private String historyEntryId;
	
	private String historyEntry;
}
