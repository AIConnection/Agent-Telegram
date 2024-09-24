package com.github.aiconnection.agents.core.bdi.pln;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private String taskName;
    private int taskId;
    private List<Result> result;
    private Metrics metrics;
    private double threshold;
}
