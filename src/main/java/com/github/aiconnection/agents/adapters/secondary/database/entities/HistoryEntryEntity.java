package com.github.aiconnection.agents.adapters.secondary.database.entities;

import java.io.Serializable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long historyEntryId;
	
	@Column(length = 1_000_000)
	private String historyEntry;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private HistoryEntity historyEntity;
}
