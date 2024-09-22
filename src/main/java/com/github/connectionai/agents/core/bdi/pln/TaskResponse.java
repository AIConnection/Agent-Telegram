package com.github.connectionai.agents.core.bdi.pln;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"failbackUserMessage"})
public class TaskResponse {
	private String failbackUserMessage;
    private List<Task> tasks;
}